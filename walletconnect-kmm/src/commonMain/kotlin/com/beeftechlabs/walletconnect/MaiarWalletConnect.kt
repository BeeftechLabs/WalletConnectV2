package com.beeftechlabs.walletconnect

import com.beeftechlabs.walletconnect.model.Connection
import com.beeftechlabs.walletconnect.model.ConnectionParams

fun WalletConnect.initMaiar(chain: String) {
    init(
        ConnectionParams(
            chain = "elrond:$chain",
            methods = ElrondNetworkMethods,
            events = emptyList()
        )
    )
}

suspend fun WalletConnect.connectMaiar(): Connection {
    return connect(
        namespace = "elrond",
        extensions = emptyList()
    )
}