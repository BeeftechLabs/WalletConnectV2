package com.beeftechlabs.walletconnectv2.util

expect object URLEncoder {
    fun encode(value: String?, charset: String): String
}

expect object URLDecoder {
    fun decode(value: String?, charset: String): String
}