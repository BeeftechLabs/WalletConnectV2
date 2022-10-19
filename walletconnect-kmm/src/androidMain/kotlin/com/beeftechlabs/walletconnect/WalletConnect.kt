package com.beeftechlabs.walletconnect

import android.app.Application
import android.util.Log
import com.beeftechlabs.walletconnect.exception.WCNotConnectedException
import com.beeftechlabs.walletconnect.model.*
import com.walletconnect.android.RelayClient
import com.walletconnect.android.connection.ConnectionType
import com.walletconnect.sign.client.Sign
import com.walletconnect.sign.client.SignClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

actual open class WalletConnect(
    application: Application,
    dapp: Dapp,
    projectId: String,
    relayUrl: String = "relay.walletconnect.com",
) : CoroutineScope {
    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.IO

    private val _events = MutableSharedFlow<WCEvent>()
    actual val events: Flow<WCEvent> = _events

    private lateinit var connectionParams: ConnectionParams

    private val dappDelegate = object : SignClient.DappDelegate {
        override fun onSessionApproved(approvedSession: Sign.Model.ApprovedSession) {
            // Triggered when Dapp receives the session approval from wallet
            launch {
                _events.emit(WCEvent.SessionApproved(approvedSession.accounts))
            }
        }

        override fun onSessionRejected(rejectedSession: Sign.Model.RejectedSession) {
            // Triggered when Dapp receives the session rejection from wallet
            launch {
                _events.emit(WCEvent.SessionRejected(rejectedSession.topic))
            }
        }

        override fun onSessionUpdate(updatedSession: Sign.Model.UpdatedSession) {
            // Triggered when Dapp receives the session update from wallet
            launch {
                _events.emit(WCEvent.SessionUpdated(updatedSession.topic))
            }
        }

        override fun onSessionExtend(session: Sign.Model.Session) {
            // Triggered when Dapp receives the session extend from wallet
            launch {
                _events.emit(WCEvent.SessionExtended(session.expiry))
            }
        }

        override fun onSessionEvent(sessionEvent: Sign.Model.SessionEvent) {
            // Triggered when the peer emits events that match the list of events agreed upon session settlement
            launch {
                _events.emit(WCEvent.SessionEvent(sessionEvent.name, sessionEvent.data))
            }
        }

        override fun onSessionDelete(deletedSession: Sign.Model.DeletedSession) {
            // Triggered when Dapp receives the session delete from wallet
            launch {
                val topic = if (deletedSession is Sign.Model.DeletedSession.Success) {
                    deletedSession.topic
                } else {
                    null
                }
                _events.emit(WCEvent.SessionDeleted(topic))
            }
        }

        override fun onSessionRequestResponse(response: Sign.Model.SessionRequestResponse) {
            // Triggered when Dapp receives the session request response from wallet
            launch {
                _events.emit(
                    WCEvent.Response(
                        response.method,
                        response.result.id,
                        response.result.jsonrpc
                    )
                )
            }
        }

        override fun onConnectionStateChange(state: Sign.Model.ConnectionState) {
            //Triggered whenever the connection state is changed
            launch {
                _events.emit(WCEvent.ConnectionChanged(state.isAvailable))
            }
        }

        override fun onError(error: Sign.Model.Error) {
            launch {
                _events.emit(WCEvent.Error(error.throwable))
            }
        }
    }

    init {
        val serverUrl = "wss://$relayUrl?projectId=${projectId}"

        RelayClient.initialize(
            relayServerUrl = serverUrl,
            connectionType = ConnectionType.AUTOMATIC,
            application = application
        )

        val appMetaData = Sign.Model.AppMetaData(
            name = dapp.name,
            description = dapp.description,
            url = dapp.url,
            icons = dapp.iconUris,
            redirect = "kotlin-dapp-wc:/request"
        )
        val init = Sign.Params.Init(relay = RelayClient, metadata = appMetaData)

        SignClient.initialize(init) { error ->
            Log.e(TAG, error.throwable.stackTraceToString())
        }

        SignClient.setDappDelegate(dappDelegate)
    }

    actual suspend fun connect(
        namespace: String,
        connectionParams: ConnectionParams,
        extensions: List<ConnectionParams>
    ): Connection = suspendCoroutine { cont ->
        this@WalletConnect.connectionParams = connectionParams

        val existingPairingTopic = SignClient.getListOfSettledPairings().firstOrNull()?.topic
//        val existingPairingTopic = null

        val proposal = namespace to Sign.Model.Namespace.Proposal(
            chains = listOf(connectionParams.chain),
            methods = connectionParams.methods.all(),
            events = connectionParams.events,
            extensions = extensions.map { ex ->
                Sign.Model.Namespace.Proposal.Extension(
                    listOf(ex.chain), ex.methods.all(), ex.events
                )
            }
        )

        val params = Sign.Params.Connect(
            namespaces = mapOf(proposal),
            pairingTopic = existingPairingTopic
        )

        SignClient.connect(
            params,
            onProposedSequence = { seq ->
                if (seq is Sign.Model.ProposedSequence.Pairing) {
                    cont.resume(Connection(seq.uri, true))
                } else {
                    cont.resume(Connection(null, true))
                }
            },
            onError = { error ->
                launch {
                    _events.emit(WCEvent.Error(error.throwable))
                }
                cont.resume(Connection(null, false))
            }
        )
    }

    actual suspend fun disconnect(topic: String?) {
        topic?.let {
            SignClient.disconnect(
                Sign.Params.Disconnect(it)
            ) {}
        } ?: SignClient.getListOfSettledPairings().forEach { pairing ->
            SignClient.disconnect(
                Sign.Params.Disconnect(pairing.topic)
            ) {}
        }
    }

    actual suspend fun login(address: String, token: String): String? {
        val data = Json.encodeToString(Login(token, address))
        return request(connectionParams.methods.SIGN_LOGIN, data)
    }

    private suspend fun request(method: String, params: String): String? {
        val success: Boolean = suspendCoroutine { cont ->
            val topic = SignClient.getListOfSettledPairings().firstOrNull()?.topic
                ?: throw WCNotConnectedException()

            SignClient.request(
                Sign.Params.Request(
                    sessionTopic = topic,
                    method = method,
                    params = params,
                    connectionParams.chain
                )
            ) {
                cont.resume(false)
            }
            cont.resume(true)
        }

        return if (success) {
            _events
                .filterIsInstance<WCEvent.Response>()
                .first { it.method == connectionParams.methods.SIGN_LOGIN }
                .data
        } else {
            null
        }
    }

    companion object {
        private const val TAG = "Signer"
    }
}