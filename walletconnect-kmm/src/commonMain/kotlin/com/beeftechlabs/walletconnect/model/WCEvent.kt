package com.beeftechlabs.walletconnect.model

sealed class WCEvent {

    data class SessionApproved(
        val accounts: List<String>
    ) : WCEvent()

    data class SessionRejected(
        val topic: String
    ) : WCEvent()

    data class SessionUpdated(
        val topic: String
    ) : WCEvent()

    data class SessionExtended(
        val expiry: Long
    ) : WCEvent()

    data class SessionEvent(
        val name: String,
        val data: String
    ) : WCEvent()

    data class SessionDeleted(
        val topic: String?
    ) : WCEvent()

    data class Response(
        val method: String,
        val id: Long,
        val data: String
    ) : WCEvent()

    data class ConnectionChanged(
        val isAvailable: Boolean
    ) : WCEvent()

    data class Error(
        val throwable: Throwable
    ) : WCEvent()
}
