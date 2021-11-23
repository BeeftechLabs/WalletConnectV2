package com.beeftechlabs.walletconnectv2.socket

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket

internal actual class PlatformSocket actual constructor(
    url: String,
    private val socketListener: PlatformSocketListener
) {
    private val socketEndpoint = url
    private var webSocket: WebSocket? = null

    actual fun openSocket() {
        val socketRequest = Request.Builder().url(socketEndpoint).build()
        val webClient = OkHttpClient().newBuilder().build()
        webSocket = webClient.newWebSocket(
            socketRequest,
            object : okhttp3.WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) = socketListener.onOpen()
                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) =
                    socketListener.onFailure(t)

                override fun onMessage(webSocket: WebSocket, text: String) =
                    socketListener.onMessage(text)

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) =
                    socketListener.onClosing(code, reason)

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) =
                    socketListener.onClosed(code, reason)
            }
        )
    }

    actual fun closeSocket(code: Int, reason: String) {
        webSocket?.close(code, reason)
        webSocket = null
    }

    actual fun sendMessage(msg: String) {
        webSocket?.send(msg)
    }

    actual fun destroy() {
    }
}