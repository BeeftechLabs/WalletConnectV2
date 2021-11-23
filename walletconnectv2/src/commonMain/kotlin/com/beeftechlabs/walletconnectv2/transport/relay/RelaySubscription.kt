package com.beeftechlabs.walletconnectv2.transport.relay

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class RelaySubscriptionRequest(
    override val id: Long,
    override val params: RelaySubscriptionParams
) : RelayRequest<RelaySubscriptionParams>() {
    @Required override val jsonrpc: String = "2.0"
    @Required override val method: String = "waku_subscription"
}

@Serializable
data class RelaySubscriptionParams(
    val id: String,
    val data: RelaySubscriptionData
)

@Serializable
data class RelaySubscriptionData(
    val topic: String,
    val message: String
)

@Serializable
data class RelaySubscriptionResponse(
    override val id: Long,
    override val result: Boolean
) : RelayResponse<Boolean>() {
    @Required override val jsonrpc: String = "2.0"
}