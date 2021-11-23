package com.beeftechlabs.walletconnectv2.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class SessionPingRequest(
    override val id: Long
) : WCRequest<List<Any>>() {
    @Required override val jsonrpc: String = "2.0"
    @Required override val method: String = "wc_sessionPing"
    @Required override val params: List<@Contextual Any> = emptyList()
}

@Serializable
data class SessionPingResponse(
    override val id: Long,
    override val result: Boolean
) : WCResponse<Boolean>() {
    @Required override val jsonrpc: String = "2.0"
}