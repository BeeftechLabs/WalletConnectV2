package com.beeftechlabs.walletconnectv2.transport.relay

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class RelayAck(
    override val id: Long,
    override val jsonrpc: String,
    @Transient val raw: String = ""
) : RelayMessage()
