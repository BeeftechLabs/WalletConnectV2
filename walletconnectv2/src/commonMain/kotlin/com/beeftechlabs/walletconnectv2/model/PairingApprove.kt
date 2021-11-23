package com.beeftechlabs.walletconnectv2.model

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class PairingApproveRequest(
    override val id: Long,
    override val params: PairingApproveParams
) : WCRequest<PairingApproveParams>() {
    @Required override val jsonrpc: String = "2.0"
    @Required override val method: String = "wc_pairingApprove"
}

@Serializable
data class PairingApproveParams(
    val topic: String,
    val relay: RelayProtocolOptions,
    val responder: Participant,
    val expiry: Long,
    val state: PairingState
)

@Serializable
data class PairingApproveResponse(
    override val id: Long,
    override val result: Boolean
) : WCResponse<Boolean>() {
    @Required override val jsonrpc: String = "2.0"
}