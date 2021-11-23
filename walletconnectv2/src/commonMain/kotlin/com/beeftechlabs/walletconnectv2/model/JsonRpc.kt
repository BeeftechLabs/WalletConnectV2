package com.beeftechlabs.walletconnectv2.model

import kotlinx.serialization.Serializable

@Serializable
data class JsonRpc constructor(
    val methods: List<String>
)