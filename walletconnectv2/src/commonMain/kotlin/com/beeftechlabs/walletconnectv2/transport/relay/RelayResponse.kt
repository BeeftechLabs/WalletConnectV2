package com.beeftechlabs.walletconnectv2.transport.relay

sealed class RelayResponse<T> : RelayMessage() {
    abstract val result: T
}
