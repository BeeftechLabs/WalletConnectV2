package com.beeftechlabs.walletconnect.model

import com.beeftechlabs.walletconnect.NetworkMethods

data class ConnectionParams(
    val chain: String,
    val methods: NetworkMethods,
    val events: List<String>,
)
