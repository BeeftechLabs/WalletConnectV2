package com.beeftechlabs.walletconnect

import com.beeftechlabs.walletconnect.model.Connection
import com.beeftechlabs.walletconnect.model.ConnectionParams
import com.beeftechlabs.walletconnect.model.WCEvent
import kotlinx.coroutines.flow.Flow

expect open class WalletConnect {
    val events: Flow<WCEvent>

    fun init(connectionParams: ConnectionParams)

    suspend fun connect(
        namespace: String,
        extensions: List<ConnectionParams>
    ): Connection

    suspend fun disconnect(topic: String?)

    suspend fun login(address: String, token: String): String

    suspend fun signMessage(message: String): String

    suspend fun signTransaction(tx: String): String
}