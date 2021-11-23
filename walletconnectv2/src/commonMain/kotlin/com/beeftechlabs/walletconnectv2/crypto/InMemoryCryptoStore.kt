package com.beeftechlabs.walletconnectv2.crypto

internal class InMemoryCryptoStore : CryptoStore {

    private val store = mutableMapOf<String, KeyPair>()

    override fun put(handle: String, keyPair: KeyPair) {
        store[handle] = keyPair
    }

    override fun get(handle: String): KeyPair? = store[handle]
}