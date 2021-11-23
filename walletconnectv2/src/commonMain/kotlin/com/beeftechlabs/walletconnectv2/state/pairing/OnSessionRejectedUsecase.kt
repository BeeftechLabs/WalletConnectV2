package com.beeftechlabs.walletconnectv2.state.pairing

import com.beeftechlabs.walletconnectv2.WCState
import com.beeftechlabs.walletconnectv2.model.SessionRejectRequest
import com.beeftechlabs.walletconnectv2.model.SessionRejectResponse
import com.beeftechlabs.walletconnectv2.state.MessageSerializer
import com.beeftechlabs.walletconnectv2.transport.Transport
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class OnSessionRejectedUsecase(
    private val transport: Transport,
    private val messageSerializer: MessageSerializer
) {

    suspend operator fun invoke(topic: String, request: SessionRejectRequest): WCState {
        transport.sendMessage(
            topic,
            Json.encodeToString(
                SessionRejectResponse(
                    request.id,
                    true
                )
            )
        )

        return WCState.SessionRejected(
            reason = request.params.reason
        )
    }
}