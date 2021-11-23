package com.beeftechlabs.walletconnectv2.util

actual object Random {
    actual fun randomUUID(): String = NSUUID().UUIDString()

    actual fun nextInt(bound: Int): Int {
        return arc4random_uniform(bound.toUInt()).toInt()
    }

    actual fun nextBytes(byteArray: ByteArray) {
        for (i in byteArray.indices) {
            byteArray[i] = arc4random().toByte()
        }
    }
}