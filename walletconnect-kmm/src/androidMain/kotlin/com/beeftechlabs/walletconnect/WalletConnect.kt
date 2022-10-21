package com.beeftechlabs.walletconnect

import android.app.Application
import android.util.Log
import com.beeftechlabs.walletconnect.exception.WCNotConnectedException
import com.beeftechlabs.walletconnect.exception.WCResponseException
import com.beeftechlabs.walletconnect.exception.WCSignatureException
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
            launch {
                _events.emit(WCEvent.SessionApproved(approvedSession.accounts))
            }
        }

        override fun onSessionRejected(rejectedSession: Sign.Model.RejectedSession) {
            launch {
                _events.emit(WCEvent.SessionRejected(rejectedSession.topic))
            }
        }

        override fun onSessionUpdate(updatedSession: Sign.Model.UpdatedSession) {
            launch {
                _events.emit(WCEvent.SessionUpdated(updatedSession.topic))
            }
        }

        override fun onSessionExtend(session: Sign.Model.Session) {
            launch {
                _events.emit(WCEvent.SessionExtended(session.expiry))
            }
        }

        override fun onSessionEvent(sessionEvent: Sign.Model.SessionEvent) {
            launch {
                _events.emit(WCEvent.SessionEvent(sessionEvent.name, sessionEvent.data))
            }
        }

        override fun onSessionDelete(deletedSession: Sign.Model.DeletedSession) {
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
            launch {
                if (response.result is Sign.Model.JsonRpcResponse.JsonRpcResult) {
                    _events.emit(
                        WCEvent.Response(
                            response.method,
                            response.result.id,
                            data = (response.result as Sign.Model.JsonRpcResponse.JsonRpcResult).result
                        )
                    )
                } else {
                    if (response.result is Sign.Model.JsonRpcResponse.JsonRpcError) {
                        val msg =
                            (response.result as Sign.Model.JsonRpcResponse.JsonRpcError).message
                        _events.emit(
                            WCEvent.Response(
                                response.method,
                                response.result.id,
                                error = msg
                            )
                        )
                        _events.emit(
                            WCEvent.Error(WCResponseException(msg))
                        )
                    }
                }
            }
        }

        override fun onConnectionStateChange(state: Sign.Model.ConnectionState) {
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
            redirect = dapp.deepLinkUri
        )
        val init = Sign.Params.Init(relay = RelayClient, metadata = appMetaData)

        SignClient.initialize(init) { error ->
            Log.e(TAG, error.throwable.stackTraceToString())
        }

        SignClient.setDappDelegate(dappDelegate)

        Log.d(TAG, "Pairings: ${SignClient.getListOfSettledPairings()}")
        Log.d(TAG, "Sessions: ${SignClient.getListOfSettledSessions()}")
    }

    actual fun init(connectionParams: ConnectionParams) {
        this.connectionParams = connectionParams
    }

    actual suspend fun connect(
        namespace: String,
        extensions: List<ConnectionParams>
    ): Connection = suspendCoroutine { cont ->

//        val existingPairingTopic = SignClient.getListOfSettledPairings().firstOrNull()?.topic
        val existingPairingTopic = null

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
        } ?: SignClient.getListOfSettledSessions().forEach { session ->
            SignClient.disconnect(
                Sign.Params.Disconnect(session.topic)
            ) {}
        }
    }

    actual suspend fun login(address: String, token: String): String {
        val data = Json.encodeToString(Login(token, address))
        return request(connectionParams.methods.SIGN_LOGIN, data) ?: throw WCSignatureException()
    }

    actual suspend fun signMessage(message: String): String {
        return request(connectionParams.methods.SIGN_MSG, message) ?: throw WCSignatureException()
    }

    actual suspend fun signTransaction(tx: String): String {
        return request(connectionParams.methods.SIGN_TX, tx) ?: throw WCSignatureException()
    }

    private suspend fun request(method: String, params: String): String? {
        val topic = SignClient.getListOfSettledSessions().firstOrNull()?.topic
            ?: throw WCNotConnectedException()

        val request = Sign.Params.Request(
            sessionTopic = topic,
            method = method,
            params = params,
            connectionParams.chain
        )

        Log.d(TAG, "Sending request $request")
        val success: Boolean = suspendCoroutine { cont ->
            var hadError = false

            SignClient.request(
                request
            ) {
                Log.e(TAG, "Request error", it.throwable)
                hadError = true
                cont.resume(false)
            }
            if (!hadError) {
                cont.resume(true)
            }
        }

        Log.d(TAG, "Request was successful: $success")

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
        private const val TAG = "WalletConnect"
    }
}