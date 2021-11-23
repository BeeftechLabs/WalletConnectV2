package com.beeftechlabs.walletconnectv2.transport.relay

import kotlinx.serialization.Serializable

sealed class RelayRequest<T> : RelayMessage() {
    abstract val method: String
    abstract val params: T
}

@Serializable
data class AbstractRelayRequest(
    val id: Long,
    val method: String
)