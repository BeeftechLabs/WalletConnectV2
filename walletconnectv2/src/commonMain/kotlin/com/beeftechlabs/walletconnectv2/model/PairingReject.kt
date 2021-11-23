package com.beeftechlabs.walletconnectv2.model

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class PairingRejectRequest(
    override val id: Long,
    override val params: PairingRejectParams
) : WCRequest<PairingRejectParams>() {
    @Required override val jsonrpc: String = "2.0"
    @Required override val method: String = "wc_pairingReject"
}

@Serializable
data class PairingRejectParams(
    val reason: String
)

@Serializable
data class PairingRejectResponse(
    override val id: Long,
    override val result: Boolean
) : WCResponse<Boolean>() {
    @Required override val jsonrpc: String = "2.0"
}