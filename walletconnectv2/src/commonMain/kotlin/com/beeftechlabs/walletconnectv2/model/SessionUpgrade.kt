package com.beeftechlabs.walletconnectv2.model

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class SessionUpgradeRequest(
    override val id: Long,
    override val params: SessionUpgradeParams
) : WCRequest<SessionUpgradeParams>() {
    @Required override val jsonrpc: String = "2.0"
    @Required override val method: String = "wc_sessionUpgrade"
}

@Serializable
data class SessionUpgradeParams(
    val permissions: SessionPermissions
)

@Serializable
data class SessionUpgradeResponse(
    override val id: Long,
    override val result: Boolean
) : WCResponse<Boolean>() {
    @Required override val jsonrpc: String = "2.0"
}