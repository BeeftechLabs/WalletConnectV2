package com.beeftechlabs.walletconnectv2.crypto

import com.beeftechlabs.walletconnectv2.exception.WCException
import com.beeftechlabs.walletconnectv2.logging.Log
import com.beeftechlabs.walletconnectv2.transport.EncryptedMessage
import com.beeftechlabs.walletconnectv2.transport.Message
import com.ionspin.kotlin.crypto.auth.Auth
import com.ionspin.kotlin.crypto.hash.Hash
import com.ionspin.kotlin.crypto.util.LibsodiumRandom
import com.ionspin.kotlin.crypto.util.hexStringToUByteArray
import com.ionspin.kotlin.crypto.util.toHexString
import com.soywiz.krypto.AES
import com.soywiz.krypto.Padding
import com.soywiz.krypto.encoding.hex
import io.ktor.utils.io.core.*

@ExperimentalUnsignedTypes
internal class TransportEncryption {

    fun encrypt(
        message: String,
        sharedKey: String,
        publicKey: String
    ): EncryptedMessage {
        val (encryptionKey, authKey) = getKeys(sharedKey)

        val data = message.toByteArray()
        val iv = LibsodiumRandom.buf(16)

        val padding = Padding.PKCS7Padding

        val encrypted =
            AES.encryptAesCbc(data, encryptionKey.toByteArray(), iv.toByteArray(), padding)

        val computedHmac = Auth.authHmacSha256(
            iv + publicKey.hexStringToUByteArray() + encrypted.toUByteArray(),
            authKey.toUByteArray()
        )
        Log.d(TAG, "Encrypting with sharedKey $sharedKey and iv ${iv.toHexString()}")

        return EncryptedMessage(
            cipherText = encrypted.hex,
            iv = iv.toHexString(),
            publicKey = publicKey,
            hmac = computedHmac.toHexString()
        ).also {
            Log.d(TAG, "sharedkey; ivl ${it.iv.length}, pkl ${publicKey.length}, hmacl ${it.hmac.length}")
        }
    }

    fun decrypt(
        encryptedMessage: EncryptedMessage,
        sharedKey: String
    ): String {
        val (encryptionKey, authKey) = getKeys(sharedKey)
        Log.d(TAG, "Decrypting with sharedKey $sharedKey and iv ${encryptedMessage.iv}")
        val encrypted = encryptedMessage.cipherText.hexStringToUByteArray()
        val iv = encryptedMessage.iv.hexStringToUByteArray()

        val computedHmac = Auth.authHmacSha256(
            iv + encryptedMessage.publicKey.hexStringToUByteArray() + encrypted,
            authKey.toUByteArray()
        )

        if (computedHmac.toHexString() != encryptedMessage.hmac) {
//            throw WCException("Invalid Hmac")
            Log.d(TAG, "Invalid Hmac")
        }

        val padding = Padding.PKCS7Padding

        return try {
            val decrypted = AES.decryptAesCbc(
                encrypted.toByteArray(),
                encryptionKey.toByteArray(),
                iv.toByteArray(),
                padding
            )
            decrypted.decodeToString()
        } catch (exception: Exception) {
            throw WCException(exception.message)
        }
    }

    private fun getKeys(sharedKey: String): Pair<UByteArray, UByteArray> {
        val hash = Hash.sha512(sharedKey.hexStringToUByteArray())

        val key = hash.take(32).toUByteArray()
        val hmac = hash.takeLast(32).toUByteArray()

        return Pair(key, hmac)
    }

    companion object {
        private const val TAG = "WC2TransportEncryption"
    }
}