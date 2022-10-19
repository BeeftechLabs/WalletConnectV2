package com.beeftechlabs.walletconnect

interface NetworkMethods {
    val SIGN_TX: String
    val SIGN_TXS: String
    val SIGN_MSG: String
    val SIGN_LOGIN: String
    val CANCEL: String

    fun all(): List<String> = listOf(
        SIGN_TX, SIGN_TXS, SIGN_MSG, SIGN_LOGIN, CANCEL
    )
}

object ElrondNetworkMethods : NetworkMethods {
    override val SIGN_TX = "erd_signTransaction"
    override val SIGN_TXS = "erd_signTransactions"
    override val SIGN_MSG = "erd_signMessage"
    override val SIGN_LOGIN = "erd_signLoginToken"
    override val CANCEL = "erd_cancelAction"
}