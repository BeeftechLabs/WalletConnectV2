package com.beeftechlabs.walletconnectv2.state

import com.beeftechlabs.walletconnectv2.logging.Log
import com.beeftechlabs.walletconnectv2.model.*
import com.beeftechlabs.walletconnectv2.transport.Message
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

internal class MessageSerializer {

    private val json = Json { ignoreUnknownKeys = true }

    fun deserialize(message: Message): WCMessage {
        Log.d(TAG, "Deserializing $message")
        try {
            val abstractRequest: WCAbstractRequest = json.decodeFromString(message.body)
            Log.d(TAG, "As abstract: $abstractRequest")

            return when (MessageMethod.fromValue(abstractRequest.method)) {
                MessageMethod.PairingApprove -> json.decodeFromString<PairingApproveRequest>(message.body)
                MessageMethod.PairingDelete -> json.decodeFromString<PairingDeleteRequest>(message.body)
                MessageMethod.PairingNotification -> json.decodeFromString<PairingNotificationRequest>(
                    message.body
                )
                MessageMethod.PairingPayload -> json.decodeFromString<PairingPayloadRequest>(message.body)
                MessageMethod.PairingPing -> json.decodeFromString<PairingPingRequest>(message.body)
                MessageMethod.PairingReject -> json.decodeFromString<PairingRejectRequest>(message.body)
                MessageMethod.PairingUpdate -> json.decodeFromString<PairingUpdateRequest>(message.body)
                MessageMethod.PairingUpgrade -> json.decodeFromString<PairingUpgradeRequest>(message.body)
                MessageMethod.SessionApprove -> json.decodeFromString<SessionApproveRequest>(message.body)
                MessageMethod.SessionDelete -> json.decodeFromString<SessionDeleteRequest>(message.body)
                MessageMethod.SessionNotification -> json.decodeFromString<SessionNotificationRequest>(
                    message.body
                )
                MessageMethod.SessionPayload -> json.decodeFromString<SessionPayloadRequest>(message.body)
                MessageMethod.SessionPing -> json.decodeFromString<SessionPingRequest>(message.body)
                MessageMethod.SessionPropose -> json.decodeFromString<SessionProposeRequest>(message.body)
                MessageMethod.SessionReject -> json.decodeFromString<SessionRejectRequest>(message.body)
                MessageMethod.SessionUpdate -> json.decodeFromString<SessionUpdateRequest>(message.body)
                MessageMethod.SessionUpgrade -> json.decodeFromString<SessionUpgradeRequest>(message.body)
            }
        } catch (exception: Exception) {
            // It's a Response
            return json.decodeFromString<WCGenericResponse>(message.body).copy(
                raw = message.body
            )
        }
    }

    companion object {
        private const val TAG = "WC2MessageSerializer"
    }
}