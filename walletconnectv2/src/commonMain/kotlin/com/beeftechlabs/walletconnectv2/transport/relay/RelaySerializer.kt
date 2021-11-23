package com.beeftechlabs.walletconnectv2.transport.relay

import com.beeftechlabs.walletconnectv2.transport.Message
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class RelaySerializer {

    private val json = Json { ignoreUnknownKeys = true }

    fun deserialize(message: String): RelayMessage {
        return try {
            val abstractRequest: AbstractRelayRequest = json.decodeFromString(message)

            when (RelayMethod.fromValue(abstractRequest.method)) {
                RelayMethod.Publish -> json.decodeFromString<RelayPublishRequest>(message)
                RelayMethod.Subscribe -> json.decodeFromString<RelaySubscribeRequest>(message)
                RelayMethod.Subscription -> json.decodeFromString<RelaySubscriptionRequest>(message)
                RelayMethod.Unsubscribe -> json.decodeFromString<RelayUnsubscribeRequest>(message)
            }
        } catch(exception: Exception) {
            // It's an Ack
            json.decodeFromString<RelayAck>(message).copy(
                raw = message
            )
        }
    }

    fun serialize(request: RelayRequest<*>) = Message(
        body = json.encodeToString(request),
        id = request.id
    )

    fun serialize(response: RelayResponse<*>) = Message(
        body = json.encodeToString(response),
        id = response.id
    )
}