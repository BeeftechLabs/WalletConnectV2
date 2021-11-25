package com.beeftechlabs.walletconnectv2.state

import com.beeftechlabs.walletconnectv2.WCState
import com.beeftechlabs.walletconnectv2.crypto.Crypto
import com.beeftechlabs.walletconnectv2.exception.WCException
import com.beeftechlabs.walletconnectv2.logging.Log
import com.beeftechlabs.walletconnectv2.model.*
import com.beeftechlabs.walletconnectv2.state.pairing.*
import com.beeftechlabs.walletconnectv2.transport.Transport
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext

internal class StateMachine(
    private val appMetadata: AppMetadata?,
    private val transport: Transport,
    private val crypto: Crypto
) : CoroutineScope {

    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Default

    private val messageSerializer = MessageSerializer()

    private val json = Json { ignoreUnknownKeys = true }

    private val onPairingApprovedUsecase = OnPairingApprovedUsecase(transport, messageSerializer)
    private val onSessionProposedUsecase = OnSessionProposedUsecase(transport, messageSerializer)
    private val onSessionApprovedUsecase = OnSessionApprovedUsecase(transport, messageSerializer)
    private val onSessionRejectedUsecase = OnSessionRejectedUsecase(transport, messageSerializer)
    private val proposeSessionUsecase = ProposeSessionUsecase(transport, messageSerializer)

    private val _state = MutableStateFlow<WCState>(WCState.Initial)
    val state: Flow<WCState> = _state

    private val _exceptions = MutableSharedFlow<WCException>()
    val exceptions: Flow<WCException> = _exceptions

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e(TAG, "exception: $exception")
        exception.printStackTrace()
        if (exception is WCException) {
            _exceptions.tryEmit(exception)
        }
    }

    private lateinit var publicKey: String
    private lateinit var peerPublicKey: String
    private lateinit var peerTopic: String

    init {
        transport.sharedKeyProvider = { topic ->
            crypto.getTopicAndSharedKey(publicKey, peerPublicKey, topic).privateKey
        }

        launch(exceptionHandler) {
            transport.messages.filter { it.body.isNotEmpty() }.collect { message ->
                when (val request = messageSerializer.deserialize(message)) {
                    is PairingApproveRequest -> {
                        if (_state.value == WCState.Pairing) {
                            this@StateMachine.peerTopic = request.params.topic
                            this@StateMachine.peerPublicKey = request.params.responder.publicKey

                            _state.value = onPairingApprovedUsecase(peerTopic, request)

                            // now propose
                            val (topic, _) = crypto.genTopicAndSharedKey(
                                publicKey,
                                request.params.responder.publicKey
                            )

                            _state.value = proposeSessionUsecase(
                                newTopic = topic,
                                peerTopic = peerTopic,
                                pairingApproveRequest = request,
                                publicKey = publicKey,
                                metadata = appMetadata
                            )
                        }
                    }
                    is PairingDeleteRequest -> {}
                    is PairingNotificationRequest -> {}
                    is PairingPayloadRequest -> {}
                    is PairingPingRequest -> {}
                    is PairingRejectRequest -> {}
                    is PairingUpdateRequest -> {}
                    is PairingUpgradeRequest -> {}
                    is SessionApproveRequest -> {
                        if (_state.value is WCState.SessionProposed) {
                            _state.value = onSessionApprovedUsecase(peerTopic, request)
                        }
                    }
                    is SessionDeleteRequest -> {}
                    is SessionNotificationRequest -> {}
                    is SessionPayloadRequest -> {}
                    is SessionPingRequest -> {}
                    is SessionProposeRequest -> {
                        if (_state.value == WCState.Paired) {
                            peerTopic = request.params.topic
                            _state.value = onSessionProposedUsecase(peerTopic, request)
                        }
                    }
                    is SessionRejectRequest -> {
                        if (_state.value is WCState.SessionProposed) {
                            _state.value = onSessionRejectedUsecase(peerTopic, request)
                        }
                    }
                    is SessionUpdateRequest -> {}
                    is SessionUpgradeRequest -> {}
                    
                    is WCGenericResponse -> {}
                    is SessionRejectResponse -> {}
                    is SessionDeleteResponse -> {}
                    is SessionApproveResponse -> {}
                    is SessionUpgradeResponse -> {}
                    is SessionUpdateResponse -> {}
                    is SessionNotificationResponse -> {}
                    is PairingNotificationResponse -> {}
                    is PairingDeleteResponse -> {}
                    is PairingRejectResponse -> {}
                    is PairingUpgradeResponse -> {}
                    is PairingUpdateResponse -> {}
                    is PairingPayloadResponse -> {}
                    is SessionProposeResponse -> {}
                    is PairingApproveResponse -> {}
                    is SessionPayloadResponse -> {}
                    is SessionPingResponse -> {}
                    is PairingPingResponse -> {}
                }
            }
        }
    }

    suspend fun pairingStarted(publicKey: String, topic: String) {
        this.publicKey = publicKey

        transport.subscribe(topic)

        _state.value = WCState.Pairing
    }

    suspend fun approvePairing(
        request: PairingApproveRequest,
        topic: String,
        publicKey: String,
        peerPublicKey: String
    ) {
        this.publicKey = publicKey
        this.peerTopic = topic
        this.peerPublicKey = peerPublicKey

        transport.subscribe(request.params.topic)

        transport.sendMessage(topic, json.encodeToString(request))
        _state.value = WCState.Paired
    }

    suspend fun approveSession(
        proposition: WCState.SessionProposed,
        accounts: List<String>
    ) {
        val sharedKey = crypto.getTopicAndSharedKey(publicKey, peerPublicKey, peerTopic).privateKey

        _state.value = onSessionProposedUsecase.approve(
            peerTopic,
            proposition.request,
            SessionParticipant(publicKey, null),
            accounts,
            publicKey,
            sharedKey
        )
    }

    suspend fun rejectSession(proposition: WCState.SessionProposed, reason: String) {
        val sharedKey = crypto.getTopicAndSharedKey(publicKey, peerPublicKey, peerTopic).privateKey

        _state.value =
            onSessionProposedUsecase.reject(
                peerTopic,
                proposition.request,
                reason,
                publicKey,
                sharedKey
            )
    }

    companion object {
        private const val TAG = "WC2StateMachine"
    }
}