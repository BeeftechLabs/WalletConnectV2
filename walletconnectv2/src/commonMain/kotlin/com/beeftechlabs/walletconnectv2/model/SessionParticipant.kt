package com.beeftechlabs.walletconnectv2.model

import kotlinx.serialization.Serializable

@Serializable
data class SessionParticipant(
    val publicKey: String,
    val metadata: AppMetadata?
)
