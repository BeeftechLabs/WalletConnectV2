package com.beeftechlabs.walletconnectv2.state.pairing

import com.beeftechlabs.walletconnectv2.WCState
import com.beeftechlabs.walletconnectv2.model.PairingApproveRequest
import com.beeftechlabs.walletconnectv2.model.PairingApproveResponse
import com.beeftechlabs.walletconnectv2.state.MessageSerializer
import com.beeftechlabs.walletconnectv2.transport.Transport
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class OnPairingApprovedUsecase(
    private val transport: Transport,
    private val messageSerializer: MessageSerializer
) {

    suspend operator fun invoke(topic: String, request: PairingApproveRequest): WCState {
        transport.sendMessage(
            topic,
            Json.encodeToString(
                PairingApproveResponse(
                    request.id,
                    true
                )
            )
        )
        return WCState.Paired
    }
}