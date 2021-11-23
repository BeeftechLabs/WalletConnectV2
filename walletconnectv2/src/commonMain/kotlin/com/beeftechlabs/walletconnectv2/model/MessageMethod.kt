package com.beeftechlabs.walletconnectv2.model

enum class MessageMethod(val value: String) {
    PairingApprove("wc_pairingApprove"),
    PairingDelete("wc_pairingDelete"),
    PairingNotification("wc_pairingNotification"),
    PairingPayload("wc_pairingPayload"),
    PairingPing("wc_pairingPing"),
    PairingReject("wc_pairingReject"),
    PairingUpdate("wc_pairingUpdate"),
    PairingUpgrade("wc_pairingUpgrade"),

    SessionApprove("wc_sessionApprove"),
    SessionDelete("wc_sessionDelete"),
    SessionNotification("wc_sessionNotification"),
    SessionPayload("wc_sessionPayload"),
    SessionPing("wc_sessionPing"),
    SessionPropose("wc_sessionPropose"),
    SessionReject("wc_sessionReject"),
    SessionUpdate("wc_sessionUpdate"),
    SessionUpgrade("wc_sessionUpgrade");

    companion object {
        fun fromValue(value: String) = values().first { it.value == value }
    }
}