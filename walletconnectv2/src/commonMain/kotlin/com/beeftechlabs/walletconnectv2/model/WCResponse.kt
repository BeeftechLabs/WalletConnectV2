package com.beeftechlabs.walletconnectv2.model

sealed class WCResponse<T> : WCMessage() {
    abstract override val jsonrpc: String
    abstract override val id: Long
    abstract val result: T
}
