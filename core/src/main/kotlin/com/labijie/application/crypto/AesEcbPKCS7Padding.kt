package com.labijie.application.crypto

import javax.crypto.Cipher

import javax.crypto.spec.SecretKeySpec




class AesEcbPKCS7Padding {

    fun encrypt(bytes: ByteArray, key: ByteArray): ByteArray {
        AesUtils.ensureBouncyCastleProvider()
        try {
            val cipher: Cipher = Cipher.getInstance(ALGORITHM, "BC")
            val keySpec = SecretKeySpec(key, "AES") //生成加密解密需要的Key
            cipher.init(Cipher.ENCRYPT_MODE, keySpec)
            return cipher.doFinal(bytes)
        } catch (e: Exception) {
            throw AesException(
                "AES ERROR",
                e
            )
        }
    }

    fun decrypt(bytes: ByteArray, key: ByteArray): ByteArray {
        AesUtils.ensureBouncyCastleProvider()
        try {
            val cipher: Cipher = Cipher.getInstance(ALGORITHM, "BC")
            val keySpec = SecretKeySpec(key, "AES") //生成加密解密需要的Key
            cipher.init(Cipher.DECRYPT_MODE, keySpec)
            return cipher.doFinal(bytes)
        } catch (e: Exception) {
            throw AesException(
                "AES ERROR",
                e
            )
        }
    }

    companion object {
        const val ALGORITHM = "AES/ECB/PKCS7Padding"
    }

}