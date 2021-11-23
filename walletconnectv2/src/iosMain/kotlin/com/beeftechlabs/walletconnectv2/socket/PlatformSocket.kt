package com.beeftechlabs.walletconnectv2.socket

import dev.peppark.partnerstaking.shared.AppState
import dev.peppark.partnerstaking.shared.util.Log
import kotlinx.atomicfu.AtomicBoolean
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import platform.Foundation.*
import platform.darwin.NSObject
import kotlin.native.concurrent.AtomicReference

internal actual class PlatformSocket actual constructor(
    url: String,
    private val socketListener: PlatformSocketListener
) : NSObject(), NSURLSessionWebSocketDelegateProtocol {

    private val TAG = "PlatformSocket"

    private val coroutineScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val socketEndpoint = NSURL.URLWithString(url)!!

    private val webSocket: AtomicReference<NSURLSessionWebSocketTask?> = AtomicReference(null)


    private val isOpen: AtomicReference<Boolean> = AtomicReference(false)

    private val messageCompletionHandler: (NSError?) -> Unit = { err ->
        err?.let { println("send msg error: $it") }
    }

    actual fun openSocket() {
        Log.d(TAG, "openSocket() called")
        val urlSession = NSURLSession.sessionWithConfiguration(
                configuration = NSURLSessionConfiguration.defaultSessionConfiguration(),
                delegate = this,
                delegateQueue = NSOperationQueue.currentQueue()
        )

        webSocket.value = urlSession.webSocketTaskWithURL(socketEndpoint)

        isOpen.value = true
        listenMessages()
        webSocket.value?.resume()

//        coroutineScope.launch {
//            AppState.state.collect {
//                when (it) {
//                    AppState.State.Foreground -> {
//                        delay(1000)
//                        if (!isOpen.value) {
//                            isOpen.value = true
//                            Log.d(TAG, "App is in FG, resuming socket")
//                            val urlSession = NSURLSession.sessionWithConfiguration(
//                                configuration = NSURLSessionConfiguration.defaultSessionConfiguration(),
//                                delegate = this@PlatformSocket,
//                                delegateQueue = NSOperationQueue.currentQueue()
//                            )
//                            webSocket.value = urlSession.webSocketTaskWithURL(socketEndpoint)
//                            listenMessages()
//                            webSocket.value?.resume()
//                        }
//                    }
//                    AppState.State.Background -> {
//                        if (isOpen.value) {
//                            isOpen.value = false
//                            Log.d(TAG, "App is in BG, cancelling socket")
//                            webSocket.value?.cancel()
//                            webSocket.value = null
//                        }
//                    }
//                }
//            }
//        }
    }

    private fun listenMessages() {
//        Log.d(TAG, "listenMessages() called")

        webSocket.value?.receiveMessageWithCompletionHandler { message, nsError ->
            when {
                nsError != null -> {
                    if (isOpen.value) {
                        socketListener.onFailure(Throwable(nsError.description))
                    }
                }
                message != null -> {
                    message.string?.let { socketListener.onMessage(it) }
                }
            }
            if (isOpen.value) {
                listenMessages()
            }
        }
    }

    actual fun closeSocket(code: Int, reason: String) {
        Log.d(TAG, "closeSocket() called with: code = $code, reason = $reason")
        webSocket.value?.finalize()
        webSocket.value?.cancelWithCloseCode(code.toLong(), null)
        webSocket.value = null
    }

    actual fun sendMessage(msg: String) {
        Log.d(TAG, "sendMessage() called")
        if (isOpen.value) {
            val message = NSURLSessionWebSocketMessage(msg)
            webSocket.value?.sendMessage(message, messageCompletionHandler)
        } else {
            Log.d(TAG, "sendMessage() skipping, websocket closed")
        }
    }

    actual fun destroy() {
        coroutineScope.cancel()
    }

    override fun URLSession(
        session: NSURLSession,
        webSocketTask: NSURLSessionWebSocketTask,
        didOpenWithProtocol: String?
    ) {
        socketListener.onOpen()
    }

    override fun URLSession(
        session: NSURLSession,
        webSocketTask: NSURLSessionWebSocketTask,
        didCloseWithCode: NSURLSessionWebSocketCloseCode,
        reason: NSData?
    ) {
        socketListener.onClosed(didCloseWithCode.toInt(), reason.toString())
    }
}
