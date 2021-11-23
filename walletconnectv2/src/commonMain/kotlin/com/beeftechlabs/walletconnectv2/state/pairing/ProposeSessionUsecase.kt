package com.beeftechlabs.walletconnectv2.state.pairing

import com.beeftechlabs.walletconnectv2.WCState
import com.beeftechlabs.walletconnectv2.exception.MissingAppMetadataException
import com.beeftechlabs.walletconnectv2.model.*
import com.beeftechlabs.walletconnectv2.state.MessageSerializer
import com.beeftechlabs.walletconnectv2.transport.Transport
import com.beeftechlabs.walletconnectv2.util.Generator
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class ProposeSessionUsecase(
    private val transport: Transport,
    private val messageSerializer: MessageSerializer
) {

    suspend operator fun invoke(
        newTopic: String,
        peerTopic: String,
        pairingApproveRequest: PairingApproveRequest,
        publicKey: String,
        metadata: AppMetadata?
    ): WCState {
        val request = SessionProposeRequest(
            id = Generator.newLongId(),
            params = SessionProposeParams(
                topic = newTopic,
                relay = RelayProtocolOptions.Default,
                proposer = SessionParticipant(
                    publicKey = publicKey,
                    metadata = metadata
                ),
                signal = SessionSignal(
                    params = SessionSignalParams(
                        topic = newTopic
                    )
                ),
                permissions = SessionPermissions(
                    jsonRpcMethods = emptyList(),
                    notificationTypes = emptyList(),
                    controllerPublicKey = pairingApproveRequest.params.responder.publicKey
                ),
                ttl = pairingApproveRequest.params.expiry
            )
        )

        transport.subscribe(newTopic)

        transport.sendMessage(
            peerTopic,
            Json.encodeToString(
                request
            )
        )

        if (metadata == null) {
            throw MissingAppMetadataException()
        }

        return WCState.SessionProposed(
            name = metadata.name,
            description = metadata.description,
            url = metadata.url,
            icons = metadata.icons,
            request = request
        )
    }
}