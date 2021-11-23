package com.beeftechlabs.walletconnectv2.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class WCGenericResponse(
    override val jsonrpc: String,
    override val id: Long,
    @Transient val raw: String = ""
) : WCMessage()
