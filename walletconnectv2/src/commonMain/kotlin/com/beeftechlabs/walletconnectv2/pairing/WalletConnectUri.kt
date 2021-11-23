package com.beeftechlabs.walletconnectv2.pairing

import com.beeftechlabs.walletconnectv2.util.URLEncoder
import io.ktor.http.*

data class WalletConnectUri internal constructor(
    internal val relay: String,
    internal val topic: String,
    internal val publickKey: String,
    internal val controller: Boolean
) {

    val pair: String = "wc://$topic@1?$RELAY=${
        URLEncoder.encode(
            relay,
            "UTF-8"
        )
    }&$PUBLIC_KEY=$publickKey&$CONTROLLER=$controller"

    val redirect: String = "wc://$topic@1"

    companion object {

        fun parse(uri: String) = with(Url(uri)) {
            WalletConnectUri(
                relay = parameters[RELAY] ?: "",
                topic = user ?: "",
                publickKey = parameters[PUBLIC_KEY] ?: "",
                controller = parameters[CONTROLLER].toBoolean()
            )
        }

        private const val RELAY = "relay"
        private const val PUBLIC_KEY = "publicKey"
        private const val CONTROLLER = "controller"
    }
}