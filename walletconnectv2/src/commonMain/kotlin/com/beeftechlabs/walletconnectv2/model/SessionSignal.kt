package com.beeftechlabs.walletconnectv2.model

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class SessionSignal(
    @Required val method: String = "pairing",
    val params: SessionSignalParams
)

@Serializable
data class SessionSignalParams(
    val topic: String
)
