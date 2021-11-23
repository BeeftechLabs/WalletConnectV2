package com.beeftechlabs.walletconnectv2.crypto

internal interface CryptoStore {

    fun put(handle: String, keyPair: KeyPair)

    fun get(handle: String): KeyPair?
}