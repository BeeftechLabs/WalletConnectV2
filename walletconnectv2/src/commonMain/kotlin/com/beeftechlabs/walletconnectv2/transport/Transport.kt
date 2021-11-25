package com.beeftechlabs.walletconnectv2.transport

import com.beeftechlabs.walletconnectv2.crypto.TransportEncryption
import com.beeftechlabs.walletconnectv2.logging.Log
import com.beeftechlabs.walletconnectv2.socket.AppSocket
import com.beeftechlabs.walletconnectv2.transport.relay.*
import com.beeftechlabs.walletconnectv2.util.Generator
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.coroutines.resume

internal class Transport {

    private val _status = MutableStateFlow(TransportStatus.Initial)
    val status: Flow<TransportStatus> = _status

    private val _messages = MutableStateFlow<Message?>(null)
    val messages: Flow<Message> = _messages.filterNotNull()

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    var relay: String = ""
        set(value) {
            field = value.replace("https://", "wss://").replace("http://", "ws://")
        }

    private val relaySerializer = RelaySerializer()

    var sharedKeyProvider: (suspend (String) -> String)? = null

    private val messageListener: ((msg: String) -> Unit) = { message ->
        Log.d(TAG, "Received message (raw): $message")
        val relayMessage = relaySerializer.deserialize(message)
        Log.d(TAG, "Received message (parsed): $relayMessage")

        when (relayMessage) {
            is RelayPublishRequest -> {}
            is RelaySubscribeRequest -> {}
            is RelaySubscriptionRequest -> {
                coroutineScope.launch {
                    val incomingMessage = relayMessage.params.data.message
                    val wcMessage = if (incomingMessage.startsWith("{")) {
                        incomingMessage
                    } else {
                        val sharedKey = sharedKeyProvider?.invoke(relayMessage.params.data.topic)
                            ?: throw IllegalArgumentException("Key Provider not set")
                        transportEncryption.decrypt(
                            EncryptedMessage.fromString(incomingMessage),
                            sharedKey
                        ).also {
                            Log.d(TAG, "Received message (decrypted): $it")
                        }
                    }
                    _messages.value = Message(relayMessage.id, wcMessage)
                }
            }
            is RelayUnsubscribeRequest -> {}
            is RelayPublishResponse -> {}
            is RelaySubscribeResponse -> {}
            is RelaySubscriptionResponse -> {}
            is RelayUnsubscribeResponse -> {}
            is RelayAck -> {
                _messages.value = Message(relayMessage.id, relayMessage.raw)
            }
        }
    }

    private val stateListener: ((AppSocket.State) -> Unit) = { state ->
        when (state) {
            AppSocket.State.CONNECTING -> _status.value = TransportStatus.Connected
            AppSocket.State.CONNECTED -> _status.value = TransportStatus.Connected
            AppSocket.State.CLOSING -> {
            }
            AppSocket.State.CLOSED -> _status.value = TransportStatus.Disconnected
        }
    }

    private val _appSocket by lazy { MutableStateFlow(AppSocket(relay, stateListener, messageListener)) }
    private val appSocket
        get() = _appSocket.value

    private val json = Json { ignoreUnknownKeys = true }

    private val transportEncryption = TransportEncryption()

    fun isConnected(): Boolean = appSocket.currentState == AppSocket.State.CONNECTED

    suspend fun connect(): Boolean {
        return if (!isConnected()) {
            suspendCancellableCoroutine { continuation ->
                coroutineScope.launch {
                    _status.collect { state ->
                        if (state == TransportStatus.Connected) {
                            _status.value = TransportStatus.Connected
                            continuation.resume(true)
                            cancel()
                        }
                    }
                }
                appSocket.connect()
            }
        } else {
            true
        }
    }

    suspend fun sendMessage(
        topic: String,
        message: String,
        publicKey: String? = null,
        sharedKey: String? = null
    ) {
        ensureConnected()

        try {
            val payload = if (publicKey != null && sharedKey != null) {
                transportEncryption.encrypt(message, sharedKey, publicKey).payload
            } else {
                message
            }

            val publishRequest = RelayPublishRequest(
                id = Generator.newLongId(),
                RelayPublishParams(
                    topic = topic,
                    message = payload,
                    ttl = 86400
                )
            )

            val encoded = json.encodeToString(publishRequest)
            Log.d(TAG, "Sending publish message: $encoded")
            appSocket.send(encoded)
            waitForAck(publishRequest.id)
        } catch (exception: Exception) {
            Log.e(TAG, "Error sending message: ${exception.message}")
        }
    }

    suspend fun subscribe(topic: String) {
        ensureConnected()

        val subscribeRequest = RelaySubscribeRequest(
            id = Generator.newLongId(),
            params = RelaySubscribeParams(
                topic
            )
        )

        try {
            val message = json.encodeToString(subscribeRequest)
            Log.d(TAG, "Sending subscribe messge: $message")
            appSocket.send(message)
            waitForAck(subscribeRequest.id)
        } catch (exception: Exception) {
            Log.e(TAG, "Error subscribing: ${exception.message}")
        }
    }

    private suspend fun ensureConnected() {
        if (
            appSocket.currentState != AppSocket.State.CONNECTED &&
            appSocket.currentState != AppSocket.State.CONNECTING
        ) {
            connect()
            delay(500)
        }
    }

    private suspend fun waitForAck(id: Long) {
        messages.first {
            it.id == id
        }
        Log.d(TAG, "Received Ack for $id")
    }

    fun close() {
        appSocket.disconnect()
        coroutineScope.cancel()
    }

    fun suspend() {
        appSocket.disconnect()
        Log.d(TAG, "Suspending")
    }

    fun resume() {
        Log.d(TAG, "Resuming")
        _appSocket.value = AppSocket(relay, stateListener, messageListener)
    }

    companion object {
        private const val TAG = "WC2Transport"
    }
}
