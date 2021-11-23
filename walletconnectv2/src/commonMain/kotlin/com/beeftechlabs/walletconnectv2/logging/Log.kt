package com.beeftechlabs.walletconnectv2.logging

object Log {

    fun d(tag: String, message: String) {
        println("$tag: $message")
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        println("$tag: $message")
        throwable?.printStackTrace()
    }
}