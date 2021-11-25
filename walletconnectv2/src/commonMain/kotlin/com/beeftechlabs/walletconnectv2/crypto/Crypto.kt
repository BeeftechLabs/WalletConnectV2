package com.beeftechlabs.walletconnectv2.crypto

import com.beeftechlabs.walletconnectv2.logging.Log
import com.ionspin.kotlin.crypto.LibsodiumInitializer
import com.ionspin.kotlin.crypto.hash.Hash
import com.ionspin.kotlin.crypto.keyexchange.KeyExchange
import com.ionspin.kotlin.crypto.util.hexStringToUByteArray
import com.ionspin.kotlin.crypto.util.toHexString

internal class Crypto(
    private val cryptoStore: CryptoStore
) {

    suspend fun genNewPublicKey(): String {
        ensureInitialized()

        val keyPair = KeyExchange.keypair()
        val pubKey = keyPair.publicKey.toHexString()
        cryptoStore.put(pubKey, KeyPair(pubKey, keyPair.secretKey.toHexString()))

        return pubKey
    }

    suspend fun genTopicAndSharedKey(publicKey: String, peerPublicKey: String, topic: String? = null): KeyPair {
        ensureInitialized()

        val (_, privateKeyHex) = cryptoStore.get(publicKey)
            ?: throw IllegalArgumentException("No private key for $publicKey")

        Log.d(TAG, "Generating sharedKey from privateKey $privateKeyHex publicKey $publicKey and peerPublicKey $peerPublicKey")

        val privateKey = privateKeyHex.hexStringToUByteArray()

        val lsKeyPair =
            if (topic == null) {
                KeyExchange.serverSessionKeys(
                    publicKey.hexStringToUByteArray(),
                    privateKey,
                    peerPublicKey.hexStringToUByteArray()
                )
            } else {
                KeyExchange.clientSessionKeys(
                    publicKey.hexStringToUByteArray(),
                    privateKey,
                    peerPublicKey.hexStringToUByteArray()
                )
            }

        val recHex = lsKeyPair.receiveKey.toHexString()
        val sendHex = lsKeyPair.sendKey.toHexString()
        Log.d(TAG, "genTopicAndSharedKey: rec $recHex send $sendHex")

        val sharedKey = if (topic == null) {
            lsKeyPair.receiveKey + lsKeyPair.sendKey
        } else {
            lsKeyPair.sendKey + lsKeyPair.receiveKey
        }

        val sharedKeyHex = sharedKey.toHexString()

        Log.d(TAG, "sharedKey is $sharedKeyHex")

        val topic = topic ?: Hash.sha256(sharedKey).toHexString()

        cryptoStore.put(topic, KeyPair(publicKey, sharedKeyHex))

        return KeyPair(topic, sharedKeyHex)
    }

    suspend fun getTopicAndSharedKey(publicKey: String, peerPublicKey: String, topic: String): KeyPair {
        return cryptoStore.get(topic)
            ?: genTopicAndSharedKey(publicKey, peerPublicKey, topic)
    }

    fun getTopicAndSharedKey(topic: String): KeyPair {
        return cryptoStore.get(topic)
            ?: throw IllegalArgumentException("No private key for $topic")
    }

    private suspend fun ensureInitialized() {
        if (!LibsodiumInitializer.isInitialized()) {
            LibsodiumInitializer.initialize()
        }
    }

    companion object {
        private const val TAG = "WC2Crypto"
    }
}