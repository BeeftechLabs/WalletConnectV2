package com.beeftechlabs.walletconnectv2.model

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class PairingDeleteRequest(
    override val id: Long,
    override val params: PairingDeleteParams
) : WCRequest<PairingDeleteParams>() {
    @Required override val jsonrpc: String = "2.0"
    @Required override val method: String = "wc_pairingDelete"
}

@Serializable
data class PairingDeleteParams(
    val reason: String
)

@Serializable
data class PairingDeleteResponse(
    override val id: Long,
    override val result: Boolean
) : WCResponse<Boolean>() {
    @Required override val jsonrpc: String = "2.0"
}