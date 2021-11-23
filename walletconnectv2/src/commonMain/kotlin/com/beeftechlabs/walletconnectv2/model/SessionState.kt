package com.beeftechlabs.walletconnectv2.model

import kotlinx.serialization.Serializable

@Serializable
data class SessionState(
    val accounts: List<String>
)
