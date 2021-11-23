package com.beeftechlabs.walletconnectv2.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class PairingPayloadRequest(
    override val id: Long,
    override val params: PairingPayloadParams
) : WCRequest<PairingPayloadParams>() {
    @Required override val jsonrpc: String = "2.0"
    @Required override val method: String = "wc_pairingPayload"
}

@Serializable
data class PairingPayloadParams(
    val request: PairingPayloadParamsRequest
)

@Serializable
data class PairingPayloadParamsRequest(
    val method: String,
    @Contextual val params: Any
)

@Serializable
data class PairingPayloadResponse(
    override val id: Long,
    @Contextual override val result: Any
) : WCResponse<Any>() {
    @Required override val jsonrpc: String = "2.0"
}