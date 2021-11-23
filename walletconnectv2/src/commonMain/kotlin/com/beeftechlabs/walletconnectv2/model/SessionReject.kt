package com.beeftechlabs.walletconnectv2.model

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class SessionRejectRequest(
    override val id: Long,
    override val params: SessionRejectParams
) : WCRequest<SessionRejectParams>() {
    @Required override val jsonrpc: String = "2.0"
    @Required override val method: String = "wc_sessionReject"
}

@Serializable
data class SessionRejectParams(
    val reason: String
)

@Serializable
data class SessionRejectResponse(
    override val id: Long,
    override val result: Boolean
) : WCResponse<Boolean>() {
    @Required override val jsonrpc: String = "2.0"
}