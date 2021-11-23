package com.beeftechlabs.walletconnectv2.util

import java.net.URLEncoder

actual object URLEncoder {
    actual fun encode(value: String?, charset: String): String =
        URLEncoder.encode(value, charset)
}

actual object URLDecoder {
    actual fun decode(value: String?, charset: String): String = "" // TODO
}