package com.beeftechlabs.walletconnectv2.transport

data class Message(
    val id: Long,
    val body: String
)

data class EncryptedMessage(
    val cipherText: String,
    val iv: String,
    val publicKey: String,
    val hmac: String
) {
    val payload: String
        get() = iv + publicKey + hmac + cipherText

    companion object {

        fun fromString(payload: String): EncryptedMessage {
            val publicKeyStartIdx = ivLength
            val hmacStartIdx = ivLength + publicKeyLength
            val textStartIdx = ivLength + publicKeyLength + macLength

            return EncryptedMessage(
                iv = payload.substring(0, publicKeyStartIdx),
                publicKey = payload.substring(publicKeyStartIdx, hmacStartIdx),
                hmac = payload.substring(hmacStartIdx, textStartIdx),
                cipherText = payload.substring(textStartIdx)
            )
        }

        private const val ivLength = 32
        private const val publicKeyLength = 64
        private const val macLength = 64
    }
}