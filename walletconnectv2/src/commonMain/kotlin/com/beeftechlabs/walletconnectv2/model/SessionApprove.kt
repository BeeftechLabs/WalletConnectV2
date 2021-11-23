package com.beeftechlabs.walletconnectv2.model

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class SessionApproveRequest(
    override val id: Long,
    override val params: SessionApproveParams
) : WCRequest<SessionApproveParams>() {
    @Required override val jsonrpc: String = "2.0"
    @Required override val method: String = "wc_sessionApprove"
}

@Serializable
data class SessionApproveParams(
    val relay: RelayProtocolOptions,
    val responder: SessionParticipant,
    val expiry: Long,
    val state: SessionState
)

@Serializable
data class SessionApproveResponse(
    override val id: Long,
    override val result: Boolean
) : WCResponse<Boolean>() {
    @Required override val jsonrpc: String = "2.0"
}