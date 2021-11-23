package com.beeftechlabs.walletconnectv2.transport.relay

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class RelayPublishRequest(
    override val id: Long,
    override val params: RelayPublishParams
) : RelayRequest<RelayPublishParams>() {
    @Required override val jsonrpc: String = "2.0"
    @Required override val method: String = "waku_publish"
}

@Serializable
data class RelayPublishParams(
    val topic: String,
    val message: String,
    val ttl: Long
)

@Serializable
data class RelayPublishResponse(
    override val id: Long,
    override val result: Boolean
) : RelayResponse<Boolean>() {
    @Required override val jsonrpc: String = "2.0"
}