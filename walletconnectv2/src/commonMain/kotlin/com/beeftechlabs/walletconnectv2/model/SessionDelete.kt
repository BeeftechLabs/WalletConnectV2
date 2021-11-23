package com.beeftechlabs.walletconnectv2.model

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class SessionDeleteRequest(
    override val id: Long,
    override val params: SessionDeleteParams
) : WCRequest<SessionDeleteParams>() {
    @Required override val jsonrpc: String = "2.0"
    @Required override val method: String = "wc_sessionDelete"
}

@Serializable
data class SessionDeleteParams(
    val reason: String
)

@Serializable
data class SessionDeleteResponse(
    override val id: Long,
    override val result: Boolean
) : WCResponse<Boolean>() {
    @Required override val jsonrpc: String = "2.0"
}