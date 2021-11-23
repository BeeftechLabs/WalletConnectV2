package com.beeftechlabs.walletconnectv2

import kotlinx.serialization.Serializable

@Serializable
class WCSessions : List<WCSession> by ArrayList()

@Serializable
data class WCSession(
    val temp: String
)