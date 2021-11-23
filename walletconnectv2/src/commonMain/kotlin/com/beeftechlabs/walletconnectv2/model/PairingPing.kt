package com.beeftechlabs.walletconnectv2.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class PairingPingRequest(
    override val id: Long
) : WCRequest<List<Any>>() {
    @Required override val jsonrpc: String = "2.0"
    @Required override val method: String = "wc_pairingPing"
    @Required override val params: List<@Contextual Any> = emptyList()
}

@Serializable
data class PairingPingResponse(
    override val id: Long,
    override val result: Boolean
) : WCResponse<Boolean>() {
    @Required override val jsonrpc: String = "2.0"
}