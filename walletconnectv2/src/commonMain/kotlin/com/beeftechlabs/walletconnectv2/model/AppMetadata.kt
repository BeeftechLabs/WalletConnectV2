package com.beeftechlabs.walletconnectv2.model

import kotlinx.serialization.Serializable

@Serializable
data class AppMetadata(
    val name: String,
    val description: String,
    val url: String,
    val icons: List<String>
)
