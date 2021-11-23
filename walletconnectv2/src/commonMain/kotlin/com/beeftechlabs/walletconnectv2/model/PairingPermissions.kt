package com.beeftechlabs.walletconnectv2.model

import kotlinx.serialization.Serializable

@Serializable
data class PairingProposedPermissions(
    val jsonrpc: JsonRpc,
    val notifications: Notifications
) {
    constructor(jsonRpcMethods: List<String>, notificationTypes: List<String>) :
            this(JsonRpc(jsonRpcMethods), Notifications(notificationTypes))
}

@Serializable
data class PairingPermissions(
    val jsonrpc: JsonRpc,
    val notifications: Notifications,
    val controller: Participant
) {
    constructor(
        jsonRpcMethods: List<String>,
        notificationTypes: List<String>,
        controllerPublicKey: String
    ) : this(
        JsonRpc(jsonRpcMethods),
        Notifications(notificationTypes),
        Participant(controllerPublicKey)
    )
}
