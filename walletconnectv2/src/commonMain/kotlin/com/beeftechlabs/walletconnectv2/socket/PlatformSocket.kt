package com.beeftechlabs.walletconnectv2.socket

internal expect class PlatformSocket(url: String, socketListener: PlatformSocketListener) {
    fun openSocket()
    fun closeSocket(code: Int, reason: String)
    fun sendMessage(msg: String)
    fun destroy()
}

interface PlatformSocketListener {
    fun onOpen()
    fun onFailure(t: Throwable)
    fun onMessage(msg: String)
    fun onClosing(code: Int, reason: String)
    fun onClosed(code: Int, reason: String)
}