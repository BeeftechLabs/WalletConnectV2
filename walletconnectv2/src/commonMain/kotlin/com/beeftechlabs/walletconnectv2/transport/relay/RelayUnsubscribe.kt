package com.beeftechlabs.walletconnectv2.transport.relay

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class RelayUnsubscribeRequest(
    override val id: Long,
    override val params: RelayUnsubscribeParams
) : RelayRequest<RelayUnsubscribeParams>() {
    @Required override val jsonrpc: String = "2.0"
    @Required override val method: String = "waku_unsubscribe"
}

@Serializable
data class RelayUnsubscribeParams(
    val topic: String,
    val id: String
)

@Serializable
data class RelayUnsubscribeResponse(
    override val id: Long,
    override val result: Boolean
) : RelayResponse<Boolean>() {
    @Required override val jsonrpc: String = "2.0"
}