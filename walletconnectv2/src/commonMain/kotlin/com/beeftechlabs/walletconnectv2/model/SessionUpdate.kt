package com.beeftechlabs.walletconnectv2.model

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class SessionUpdateRequest(
    override val id: Long,
    override val params: SessionUpdateParams
) : WCRequest<SessionUpdateParams>() {
    @Required override val jsonrpc: String = "2.0"
    @Required override val method: String = "wc_sessionUpdate"
}

@Serializable
data class SessionUpdateParams(
    val state: SessionState
)

@Serializable
data class SessionUpdateResponse(
    override val id: Long,
    override val result: Boolean
) : WCResponse<Boolean>() {
    @Required override val jsonrpc: String = "2.0"
}