package com.beeftechlabs.walletconnectv2.model

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class PairingUpdateRequest(
    override val id: Long,
    override val params: PairingUpdateParams
) : WCRequest<PairingUpdateParams>() {
    @Required override val jsonrpc: String = "2.0"
    @Required override val method: String = "wc_pairingUpdate"
}

@Serializable
data class PairingUpdateParams(
    val state: PairingState
)

@Serializable
data class PairingUpdateResponse(
    override val id: Long,
    override val result: Boolean
) : WCResponse<Boolean>() {
    @Required override val jsonrpc: String = "2.0"
}