package com.beeftechlabs.walletconnectv2.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class SessionPayloadRequest(
    override val id: Long,
    override val params: SessionPayloadParams
) : WCRequest<SessionPayloadParams>() {
    @Required override val jsonrpc: String = "2.0"
    @Required override val method: String = "wc_sessionPayload"
}

@Serializable
data class SessionPayloadParams(
    val request: SessionPayloadParamsRequest,
    val chainId: String?
)

@Serializable
data class SessionPayloadParamsRequest(
    val method: String,
    @Contextual val params: Any
)

@Serializable
data class SessionPayloadResponse(
    override val id: Long,
    @Contextual override val result: Any
) : WCResponse<Any>() {
    @Required override val jsonrpc: String = "2.0"
}