package com.beeftechlabs.walletconnectv2.crypto

import com.beeftechlabs.walletconnectv2.logging.Log
import com.ionspin.kotlin.crypto.LibsodiumInitializer
import com.ionspin.kotlin.crypto.hash.Hash
import com.ionspin.kotlin.crypto.scalarmult.ScalarMultiplication
import com.ionspin.kotlin.crypto.signature.Signature
import com.ionspin.kotlin.crypto.util.hexStringToUByteArray
import com.ionspin.kotlin.crypto.util.toHexString

internal class Crypto(
    private val cryptoStore: CryptoStore
) {

    suspend fun genNewPublicKey(): String {
        ensureInitialized()

        val lsKeyPair = Signature.keypair()
        val pubKey = Signature.ed25519PkToCurve25519(lsKeyPair.publicKey).toHexString()
        val sKey = Signature.ed25519SkToCurve25519(lsKeyPair.secretKey).toHexString()

        cryptoStore.put(pubKey, KeyPair(pubKey, sKey))

        return pubKey
    }

    suspend fun genTopicAndSharedKey(publicKey: String, peerPublicKey: String): KeyPair {
        ensureInitialized()

        val (_, privateKeyHexed) = cryptoStore.get(publicKey)
            ?: throw IllegalArgumentException("No private key for $publicKey")

        Log.d(TAG, "Generating sharedKey from privateKey $privateKeyHexed publicKey $publicKey and peerPublicKey $peerPublicKey")

        val privateKey = privateKeyHexed.hexStringToUByteArray()

        val sharedKey = ScalarMultiplication.scalarMultiplication(
            privateKey,
            peerPublicKey.hexStringToUByteArray()
        )

        val topic = Hash.sha256(sharedKey).toHexString()

        val sharedKeyHex = sharedKey.toHexString()

        Log.d(TAG, "sharedKey is $sharedKeyHex")

        cryptoStore.put(topic, KeyPair(sharedKeyHex, publicKey))

        return KeyPair(topic, sharedKeyHex)
    }

    suspend fun getSharedKey(publicKey: String, peerPublicKey: String): String {
        ensureInitialized()

        val (_, privateKey) = cryptoStore.get(publicKey)
            ?: throw IllegalArgumentException("No private key for $publicKey")

        return ScalarMultiplication.scalarMultiplication(
            privateKey.hexStringToUByteArray(),
            peerPublicKey.hexStringToUByteArray()
        ).toHexString()
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