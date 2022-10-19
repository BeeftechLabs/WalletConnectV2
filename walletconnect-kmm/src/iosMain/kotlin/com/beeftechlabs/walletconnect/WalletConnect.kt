package com.beeftechlabs.walletconnect

import com.beeftechlabs.walletconnect.model.Connection
import com.beeftechlabs.walletconnect.model.ConnectionParams
import com.beeftechlabs.walletconnect.model.WCEvent
import kotlinx.coroutines.flow.Flow

actual open class WalletConnect {
    actual val events: Flow<WCEvent>
        get() = TODO("Not yet implemented")

    actual suspend fun connect(
        namespace: String,
        connectionParams: ConnectionParams,
        extensions: List<ConnectionParams>
    ): Connection {
        TODO("Not yet implemented")
    }

    actual suspend fun disconnect(topic: String?) {
    }

    actual suspend fun login(address: String, token: String): String? {
        TODO("Not yet implemented")
    }
}