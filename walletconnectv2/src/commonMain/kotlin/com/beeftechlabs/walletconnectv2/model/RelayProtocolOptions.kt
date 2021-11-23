package com.beeftechlabs.walletconnectv2.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class RelayProtocolOptions(
    val protocol: String,
    val params: Map<String, @Contextual Any>
) {
    companion object {
        val Default = RelayProtocolOptions(
            protocol = "waku",
            params = emptyMap()
        )
    }
}
