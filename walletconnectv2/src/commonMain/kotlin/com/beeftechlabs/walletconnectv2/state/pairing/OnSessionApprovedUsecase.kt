package com.beeftechlabs.walletconnectv2.state.pairing

import com.beeftechlabs.walletconnectv2.WCState
import com.beeftechlabs.walletconnectv2.model.SessionApproveRequest
import com.beeftechlabs.walletconnectv2.model.SessionApproveResponse
import com.beeftechlabs.walletconnectv2.state.MessageSerializer
import com.beeftechlabs.walletconnectv2.transport.Transport
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class OnSessionApprovedUsecase(
    private val transport: Transport,
    private val messageSerializer: MessageSerializer
) {

    suspend operator fun invoke(topic: String, request: SessionApproveRequest): WCState {
        transport.sendMessage(
            topic,
            Json.encodeToString(
                SessionApproveResponse(
                    request.id,
                    true
                )
            )
        )

        return WCState.SessionApproved(
            accounts = request.params.state.accounts
        )
    }
}