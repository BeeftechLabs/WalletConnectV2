package com.beeftechlabs.walletconnectv2.transport.relay

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class RelaySubscribeRequest(
    override val id: Long,
    override val params: RelaySubscribeParams
) : RelayRequest<RelaySubscribeParams>() {
    @Required override val jsonrpc: String = "2.0"
    @Required override val method: String = "waku_subscribe"
}

@Serializable
data class RelaySubscribeParams(
    val topic: String
)

@Serializable
data class RelaySubscribeResponse(
    override val id: Long,
    override val result: String
) : RelayResponse<String>() {
    @Required override val jsonrpc: String = "2.0"
}