package com.beeftechlabs.walletconnectv2.model

import kotlinx.serialization.Serializable

@Serializable
data class SessionProposedPermissions(
    val blockchain: Blockchain,
    val jsonrpc: JsonRpc,
    val notifications: Notifications
) {
    constructor(
        chains: List<String>,
        jsonRpcMethods: List<String>,
        notificationTypes: List<String>
    ) : this(Blockchain(chains), JsonRpc(jsonRpcMethods), Notifications(notificationTypes))
}

@Serializable
class SessionPermissions(
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
