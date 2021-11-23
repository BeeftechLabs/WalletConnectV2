package com.beeftechlabs.walletconnectv2.crypto

import kotlinx.serialization.Serializable

@Serializable
data class KeyPair(
    val publicKey: String,
    val privateKey: String
)
