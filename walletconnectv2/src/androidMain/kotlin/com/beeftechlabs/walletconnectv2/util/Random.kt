package com.beeftechlabs.walletconnectv2.util

actual object Random {

    actual fun randomUUID(): String = java.util.UUID.randomUUID().toString()

    actual fun nextInt(bound: Int): Int = java.util.Random().nextInt(bound)

    actual fun nextBytes(byteArray: ByteArray) = java.util.Random().nextBytes(byteArray)
}