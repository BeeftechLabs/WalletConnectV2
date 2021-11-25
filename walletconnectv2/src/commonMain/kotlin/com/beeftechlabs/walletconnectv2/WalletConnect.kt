package com.beeftechlabs.walletconnectv2

import com.beeftechlabs.walletconnectv2.crypto.Crypto
import com.beeftechlabs.walletconnectv2.crypto.InMemoryCryptoStore
import com.beeftechlabs.walletconnectv2.exception.WCException
import com.beeftechlabs.walletconnectv2.model.*
import com.beeftechlabs.walletconnectv2.pairing.WalletConnectUri
import com.beeftechlabs.walletconnectv2.state.StateMachine
import com.beeftechlabs.walletconnectv2.transport.Transport
import com.beeftechlabs.walletconnectv2.util.Generator
import com.ionspin.kotlin.crypto.hash.Hash
import com.ionspin.kotlin.crypto.util.hexStringToUByteArray
import com.ionspin.kotlin.crypto.util.toHexString
import io.ktor.util.date.*
import kotlinx.coroutines.flow.Flow

class WalletConnect(
    appMetadata: AppMetadata? = null
) {

    private val crypto = Crypto(InMemoryCryptoStore())

    private val transport = Transport()

    private val stateMachine = StateMachine(appMetadata, transport, crypto)

    val state: Flow<WCState> = stateMachine.state
    val exceptions: Flow<WCException> = stateMachine.exceptions

    suspend fun newConnection(relay: String, controller: Boolean): WalletConnectUri {
        transport.relay = relay

        val publicKey = crypto.genNewPublicKey()
        val topic = Hash.sha256(publicKey.hexStringToUByteArray()).toHexString()

        stateMachine.pairingStarted(publicKey, topic)

        return WalletConnectUri(
            relay = relay,
            topic = topic,
            publickKey = publicKey,
            controller = controller
        )
    }

    suspend fun pair(uri: WalletConnectUri) {
        transport.relay = uri.relay

        val publicKey = crypto.genNewPublicKey()
        val expiry: Long = getTimeMillis() / 1000 + 30 * 24 * 60 * 60
        val peerPublicKey = uri.publickKey

        val (topic, _) = crypto.genTopicAndSharedKey(publicKey, peerPublicKey, uri.topic)

        val approve = PairingApproveRequest(
            id = Generator.newLongId(),
            params = PairingApproveParams(
                topic = topic,
                relay = RelayProtocolOptions.Default,
                responder = Participant(publicKey),
                expiry = expiry,
                state = PairingState(null)
            )
        )

        stateMachine.approvePairing(approve, uri.topic, publicKey, peerPublicKey)
    }

    suspend fun approve(proposition: WCState.SessionProposed, accounts: List<String>) {
        stateMachine.approveSession(proposition, accounts)
    }

    suspend fun reject(proposition: WCState.SessionProposed, reason: String) {
        stateMachine.rejectSession(proposition, reason)
    }

    fun pairings(): List<WCPairing> {
        // todo
        return emptyList()
    }

    fun sessions(): List<WCSession> {
        // todo
        return emptyList()
    }
}