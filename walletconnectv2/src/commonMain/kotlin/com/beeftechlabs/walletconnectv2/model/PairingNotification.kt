package com.beeftechlabs.walletconnectv2.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class PairingNotificationRequest(
    override val id: Long,
    override val params: PairingNotificationParams
) : WCRequest<PairingNotificationParams>() {
    @Required override val jsonrpc: String = "2.0"
    @Required override val method: String = "wc_pairingNotification"
}

@Serializable
data class PairingNotificationParams(
    val type: String,
    @Contextual val data: Any
)

@Serializable
data class PairingNotificationResponse(
    override val id: Long,
    override val result: Boolean
) : WCResponse<Boolean>() {
    @Required override val jsonrpc: String = "2.0"
}