package com.beeftechlabs.walletconnect.model

import kotlinx.serialization.Serializable

@Serializable
data class Login(
    val token: String,
    val address: String
)