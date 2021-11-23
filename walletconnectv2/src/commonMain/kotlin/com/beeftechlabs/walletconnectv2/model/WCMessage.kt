package com.beeftechlabs.walletconnectv2.model

sealed class WCMessage {
    abstract val jsonrpc: String
    abstract val id: Long
}
