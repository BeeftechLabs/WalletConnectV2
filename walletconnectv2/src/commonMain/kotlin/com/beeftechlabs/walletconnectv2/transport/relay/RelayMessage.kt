package com.beeftechlabs.walletconnectv2.transport.relay

import kotlinx.serialization.Serializable

@Serializable
sealed class RelayMessage {
    abstract val jsonrpc: String

    abstract val id: Long
}