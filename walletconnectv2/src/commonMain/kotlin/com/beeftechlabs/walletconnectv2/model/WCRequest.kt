package com.beeftechlabs.walletconnectv2.model

import kotlinx.serialization.Serializable

@Serializable
data class WCAbstractRequest(
    val id: Long,
    val method: String
)

sealed class WCRequest<T> : WCMessage() {
    abstract override val jsonrpc: String
    abstract override val id: Long
    abstract val method: String
    abstract val params: T
}