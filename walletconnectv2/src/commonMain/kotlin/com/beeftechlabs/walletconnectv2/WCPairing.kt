package com.beeftechlabs.walletconnectv2

import kotlinx.serialization.Serializable

@Serializable
class WCPairings : List<WCPairing> by ArrayList()

@Serializable
data class WCPairing(
    val temp: String
)