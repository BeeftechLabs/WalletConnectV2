package com.beeftechlabs.walletconnectv2.util

import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.stringByAddingPercentEscapesUsingEncoding

actual object URLEncoder {
    actual fun encode(value: String?, charset: String): String =
        (value as NSString?)?.stringByAddingPercentEscapesUsingEncoding(NSUTF8StringEncoding) ?: ""
}

actual object URLDecoder {
    actual fun decode(value: String?, charset: String): String = "" // TODO
}