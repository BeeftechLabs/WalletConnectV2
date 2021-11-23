package com.beeftechlabs.walletconnectv2.model

import kotlinx.serialization.Serializable

@Serializable
data class Blockchain(
    val chains: List<String>
)
