package com.beeftechlabs.walletconnectv2.model

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class SessionProposeRequest(
    override val id: Long,
    override val params: SessionProposeParams
) : WCRequest<SessionProposeParams>() {
    @Required override val jsonrpc: String = "2.0"
    @Required override val method: String = "wc_sessionPropose"
}

@Serializable
data class SessionProposeParams(
    val topic: String,
    val relay: RelayProtocolOptions,
    val proposer: SessionParticipant,
    val signal: SessionSignal,
    val permissions: SessionPermissions,
    val ttl: Long
)

@Serializable
data class SessionProposeResponse(
    override val id: Long,
    override val result: Boolean
) : WCResponse<Boolean>() {
    @Required override val jsonrpc: String = "2.0"
}