package com.beeftechlabs.walletconnectv2.socket

class AppSocket(
    url: String,
    private val stateListener: (State) -> Unit,
    private val messageListener: (msg: String) -> Unit
) {

    private val socketListener: PlatformSocketListener = object : PlatformSocketListener {
        override fun onOpen() {
            updateState(State.CONNECTED)
        }
        override fun onFailure(t: Throwable) {
            socketError = t
            updateState(State.CLOSED)
        }
        override fun onMessage(msg: String) {
            messageListener(msg)
        }
        override fun onClosing(code: Int, reason: String) {
            updateState(State.CLOSING)
        }
        override fun onClosed(code: Int, reason: String) {
            updateState(State.CLOSED)
        }
    }

    private val ws = PlatformSocket(url, socketListener)

    var socketError: Throwable? = null

    var currentState: State = State.CLOSED

    fun connect() {
        if (currentState != State.CLOSED) {
            throw IllegalStateException("The socket is available.")
        }
        socketError = null
        updateState(State.CONNECTING)
        ws.openSocket()
    }

    fun disconnect() {
        if (currentState != State.CLOSED) {
            updateState(State.CLOSING)
            ws.closeSocket(1000, "The user has closed the connection.")
            ws.destroy()
        }
    }

    fun send(msg: String) {
        if (currentState != State.CONNECTED) throw IllegalStateException("The connection is lost.")
        ws.sendMessage(msg)
    }

    private fun updateState(newState: State) {
        currentState = newState
        stateListener(newState)
    }

    enum class State {
        CONNECTING,
        CONNECTED,
        CLOSING,
        CLOSED
    }

    companion object {
        private const val TAG = "AppSocket"
    }
}