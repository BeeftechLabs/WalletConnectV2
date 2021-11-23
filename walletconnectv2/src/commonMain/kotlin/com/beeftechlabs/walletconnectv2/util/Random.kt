package com.beeftechlabs.walletconnectv2.util

expect object Random {

    fun randomUUID(): String

    fun nextInt(bound: Int): Int

    fun nextBytes(byteArray: ByteArray)
}