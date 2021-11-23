package com.beeftechlabs.walletconnectv2.transport.relay

enum class RelayMethod(val value: String) {
    Publish("waku_publish"),
    Subscribe("waku_subscribe"),
    Subscription("waku_subscription"),
    Unsubscribe("waku_unsubscribe");

    companion object {
        fun fromValue(value: String) = values().first { it.value == value }
    }
}