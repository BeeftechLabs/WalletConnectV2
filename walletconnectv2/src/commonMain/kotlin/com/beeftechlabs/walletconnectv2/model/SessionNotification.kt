package com.beeftechlabs.walletconnectv2.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class SessionNotificationRequest(
    override val id: Long,
    override val params: SessionNotificationParams
) : WCRequest<SessionNotificationParams>() {
    @Required override val jsonrpc: String = "2.0"
    @Required override val method: String = "wc_sessionNotification"
}

@Serializable
data class SessionNotificationParams(
    val type: String,
    @Contextual val data: Any
)

@Serializable
data class SessionNotificationResponse(
    override val id: Long,
    override val result: Boolean
) : WCResponse<Boolean>() {
    @Required override val jsonrpc: String = "2.0"
}