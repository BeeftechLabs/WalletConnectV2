package com.beeftechlabs.walletconnectv2.model

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class PairingUpgradeRequest(
    override val id: Long,
    override val params: PairingUpgradeParams
) : WCRequest<PairingUpgradeParams>() {
    @Required override val jsonrpc: String = "2.0"
    @Required override val method: String = "wc_pairingUpgrade"
}

@Serializable
data class PairingUpgradeParams(
    val permissions: PairingPermissions
)

@Serializable
data class PairingUpgradeResponse(
    override val id: Long,
    override val result: Boolean
) : WCResponse<Boolean>() {
    @Required override val jsonrpc: String = "2.0"
}