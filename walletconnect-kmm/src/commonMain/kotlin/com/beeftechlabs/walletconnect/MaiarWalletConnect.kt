package com.beeftechlabs.walletconnect

import com.beeftechlabs.walletconnect.model.Connection
import com.beeftechlabs.walletconnect.model.ConnectionParams

suspend fun WalletConnect.connectMaiar(chain: String): Connection {
    return connect(
        "elrond",
        ConnectionParams(
            chain = "elrond:$chain",
            methods = ElrondNetworkMethods,
            events = emptyList()
        ),
        emptyList()
    )
}