package com.beeftechlabs.walletconnectv2.state.pairing

import com.beeftechlabs.walletconnectv2.WCState
import com.beeftechlabs.walletconnectv2.exception.MissingAppMetadataException
import com.beeftechlabs.walletconnectv2.model.*
import com.beeftechlabs.walletconnectv2.state.MessageSerializer
import com.beeftechlabs.walletconnectv2.transport.Transport
import io.ktor.util.date.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class OnSessionProposedUsecase(
    private val transport: Transport,
    private val messageSerializer: MessageSerializer
) {

    suspend fun approve(
        topic: String,
        request: SessionProposeRequest,
        self: SessionParticipant,
        accounts: List<String>,
        publicKey: String,
        sharedKey: String
    ): WCState {
        transport.sendMessage(
            topic,
            Json.encodeToString(
                SessionApproveRequest(
                    id = request.id,
                    params = SessionApproveParams(
                        relay = request.params.relay,
                        responder = self,
                        expiry = getTimeMillis() / 1000 + request.params.ttl,
                        state = SessionState(accounts)
                    )
                )
            ),
            publicKey,
            sharedKey
        )

        return WCState.SessionApproved(accounts)
    }

    suspend fun reject(
        topic: String,
        request: SessionProposeRequest,
        reason: String,
        publicKey: String,
        sharedKey: String
    ): WCState {
        transport.sendMessage(
            topic,
            Json.encodeToString(
                SessionRejectRequest(
                    id = request.id,
                    params = SessionRejectParams(
                        reason = reason
                    )
                )
            ),
            publicKey,
            sharedKey
        )

        return WCState.SessionRejected(reason)
    }

    suspend operator fun invoke(
        topic: String,
        request: SessionProposeRequest
    ): WCState {
        transport.sendMessage(
            topic,
            Json.encodeToString(
                SessionProposeResponse(
                    request.id,
                    true
                )
            )
        )

        if (request.params.proposer.metadata == null) {
            throw MissingAppMetadataException()
        }

        return WCState.SessionProposed(
            name = request.params.proposer.metadata.name,
            description = request.params.proposer.metadata.description,
            url = request.params.proposer.metadata.url,
            icons = request.params.proposer.metadata.icons,
            request = request
        )
    }
}