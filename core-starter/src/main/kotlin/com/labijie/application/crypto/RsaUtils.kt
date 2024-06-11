package com.labijie.application.crypto

import com.labijie.infra.utils.throwIfNecessary
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher


/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-12
 */
object RsaUtils {
    /**
     * RSA最大加密明文大小
     */
    private const val MAX_ENCRYPT_BLOCK = 117

    const val ENCRTPY_RSA = "RSA/ECB/PKCS1Padding"

    const val SIGN_SHA256RSA_ALGORITHMS = "SHA256WithRSA"

    const val SIGN_ALGORITHMS = "SHA1WithRSA"
    /**
     * @param sortedParams
     * @return
     */
    private fun getSignContent(sortedParams: Map<String, String>): String {
        val content = StringBuilder()
        val keys: List<String> = ArrayList(sortedParams.keys).sorted()
        keys.forEachIndexed { index, key ->
            val value = sortedParams[key]
            if (key.isNotBlank() && !value.isNullOrBlank()) {
                content.append(if (index == 0) "" else "&").append("$key=$value")
            }
        }
        return content.toString()
    }


    fun rsaSignSHA256(
        content: String, privateKey: String,
        charset: Charset = Charsets.UTF_8
    ): String {
        return privateKey.let {
            val priKey = getPrivateKeyFromPKCS8(
                privateKey.toByteArray(Charsets.UTF_8)
            )
            rsaSignSHA256(content, priKey, charset)
        }
    }

    fun rsaSignSHA1(
        content: String, privateKey: String,
        charset: Charset = Charsets.UTF_8
    ): String {
        return privateKey.let {
            val priKey = getPrivateKeyFromPKCS8(
                privateKey.toByteArray(Charsets.UTF_8)
            )
            rsaSignSHA1(content, priKey, charset)
        }
    }

    fun rsaSignSHA1(
        params: Map<String, String>, privateKey: String,
        charset: Charset = Charsets.UTF_8
    ): String {
        val signContent = getSignContent(params)
        return rsaSignSHA1(signContent, privateKey, charset)
    }

    fun rsaSignSHA256(
        params: Map<String, String>, privateKey: String,
        charset: Charset = Charsets.UTF_8
    ): String {
        val signContent = getSignContent(params)
        return rsaSignSHA256(
            signContent,
            privateKey,
            charset
        )
    }

    fun rsaSignSHA1(
        params: Map<String, String>, privateKey: RSAPrivateKey,
        charset: Charset = Charsets.UTF_8
    ): String {
        val signContent = getSignContent(params)
        return rsaSignSHA1(signContent, privateKey, charset)
    }

    fun rsaSignSHA256(
        params: Map<String, String>, privateKey: RSAPrivateKey,
        charset: Charset = Charsets.UTF_8
    ): String {
        val signContent = getSignContent(params)
        return rsaSignSHA256(
            signContent,
            privateKey,
            charset
        )
    }

    fun rsaSignSHA1(
        content: String, privateKey: RSAPrivateKey,
        charset: Charset = Charsets.UTF_8
    ): String {
        return try {
            val signature: Signature = Signature
                .getInstance(SIGN_ALGORITHMS)
            signature.initSign(privateKey)
            signature.update(content.toByteArray(charset))
            val signed: ByteArray = signature.sign()
            Base64.getEncoder().encodeToString(signed)
        } catch (ie: InvalidKeySpecException) {
            throw RsaException(
                "RSA private key format is not correct, check that the PKCS8 format is configured correctly",
                ie
            )
        } catch (e: Exception) {
            throw RsaException(
                "RSAcontent = $content; charset = $charset",
                e
            )
        }
    }

    fun rsaSignSHA256(
        content: String, privateKey: PrivateKey,
        charset: Charset = Charsets.UTF_8
    ): String {
        return try {
            val signature: Signature = Signature.getInstance(SIGN_SHA256RSA_ALGORITHMS)
            signature.initSign(privateKey)
            signature.update(content.toByteArray(charset))
            val signed: ByteArray = signature.sign()
            Base64.getEncoder().encodeToString(signed)
        } catch (e: Exception) {
            e.throwIfNecessary()
            throw RsaException(
                "RSAcontent = $content; charset = $charset",
                e
            )
        }
    }

    private fun getPublicKeyFromX509(
        keyContent: ByteArray
    ): RSAPublicKey {
        val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
        val encodedKey = Base64.getDecoder().decode(keyContent)
        return keyFactory.generatePublic(X509EncodedKeySpec(encodedKey)) as RSAPublicKey
    }

    private fun getPrivateKeyFromPKCS8(
        keyContent: ByteArray
    ): RSAPrivateKey {
        if (keyContent.isEmpty()) {
            throw RsaException("Key or algorithm cant not be null.")
        }
        val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
        val key = Base64.getDecoder().decode(keyContent)
        val spec = PKCS8EncodedKeySpec(key)
        return keyFactory.generatePrivate(spec) as RSAPrivateKey
    }

    fun verifySHA1(
        context: Map<String, String>,
        sign: String,
        publicKey: String,
        charset: Charset = Charsets.UTF_8
    ): Boolean {
        val str = getSignContent(context)
        return verifySHA1(str, sign, publicKey, charset)
    }

    fun verifySHA256(
        context: Map<String, String>,
        sign: String,
        publicKey: String,
        charset: Charset = Charsets.UTF_8
    ): Boolean {
        val str = getSignContent(context)
        return verifySHA256(str, sign, publicKey, charset)
    }

    fun verifySHA1(
        content: String, sign: String, publicKey: String,
        charset: Charset = Charsets.UTF_8
    ): Boolean {
        return publicKey.let {
            val pubKey = getPublicKeyFromX509(
                publicKey.toByteArray(Charsets.UTF_8)
            )
            verifySHA1(content, sign, pubKey, charset)
        }
    }

    fun verifySHA256(
        content: String,
        sign: String,
        publicKey: String,
        charset: Charset = Charsets.UTF_8
    ): Boolean {
        return publicKey.let {
            val pubKey = getPublicKeyFromX509(
                publicKey.toByteArray(Charsets.UTF_8)
            )
            verifySHA256(content, sign, pubKey, charset)
        }
    }

    fun verifySHA1(
        content: String, sign: String, publicKey: RSAPublicKey,
        charset: Charset = Charsets.UTF_8
    ): Boolean {
        return try {
            val signature: Signature = Signature
                .getInstance(SIGN_ALGORITHMS)
            signature.initVerify(publicKey)
            signature.update(content.toByteArray(charset))
            signature.verify(Base64.getDecoder().decode(sign))
        } catch (e: Exception) {
            e.throwIfNecessary()
            throw RsaException(
                "RSAcontent = $content,sign=$sign,charset = $charset", e
            )
        }
    }

    fun verifySHA256(
        content: String,
        sign: String,
        publicKey: RSAPublicKey,
        charset: Charset = Charsets.UTF_8
    ): Boolean {
        return try {
            val signature: Signature = Signature
                .getInstance(SIGN_SHA256RSA_ALGORITHMS)
            signature.initVerify(publicKey)
            signature.update(content.toByteArray(charset))
            signature.verify(Base64.getDecoder().decode(sign))
        } catch (e: Exception) {
            e.throwIfNecessary()
            throw RsaException(
                "RSAcontent = $content,sign=$sign,charset = $charset",
                e
            )
        }
    }


    fun encrypt(
        content: String, publicKey: String,
        charset: Charset = Charsets.UTF_8
    ): String {
        val pubKey = getPublicKeyFromX509(
            publicKey.toByteArray(Charsets.UTF_8)
        )
        return encrypt(content, pubKey, charset)
    }

    fun encrypt(
        content: String, publicKey: RSAPublicKey,
        charset: Charset = Charsets.UTF_8
    ): String {
        return try {
            val cipher: Cipher = Cipher.getInstance(ENCRTPY_RSA)
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            val data = content.toByteArray(Charsets.UTF_8)
            val inputLen = data.size
            var offSet = 0
            var i = 0
            val bytes = ByteArrayOutputStream().use { out ->
                // 对数据分段加密
                while (inputLen - offSet > 0) {
                    val buffer = if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                        cipher.doFinal(data, offSet,
                            MAX_ENCRYPT_BLOCK
                        )
                    } else {
                        cipher.doFinal(data, offSet, inputLen - offSet)
                    }
                    out.write(buffer, 0, buffer.size)
                    i++
                    offSet = i * MAX_ENCRYPT_BLOCK
                }
                val encryptedData = out.toByteArray()
                Base64.getEncoder().encode(encryptedData)
            }
            bytes.toString(charset)
        } catch (e: Exception) {
            e.throwIfNecessary()
            throw RsaException(
                "EncryptContent = $content,charset = $charset",
                e
            )
        }
    }

    fun decrypt(
        content: String,
        privateKey: String,
        charset: Charset = Charsets.UTF_8
    ): String {
        val priKey = getPrivateKeyFromPKCS8(privateKey.toByteArray(Charsets.UTF_8))
        return decrypt(content, priKey, charset)
    }


    fun decrypt(
        content: String,
        privateKey: RSAPrivateKey,
        charset: Charset = Charsets.UTF_8
    ): String {
        return try {
            //填充问题处理， 1024 密钥使用 128 个字节

            val blockSize = privateKey.modulus.bitLength() / 8
            val cipher: Cipher = Cipher.getInstance(ENCRTPY_RSA)
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            val bytes = content.toByteArray(charset)
            val encryptedData: ByteArray = Base64.getDecoder().decode(bytes)
            val inputLen = encryptedData.size
            var offSet = 0
            var cache: ByteArray
            var i = 0
            val decryptedData = ByteArrayOutputStream().use { out ->
                // 对数据分段解密
                while (inputLen - offSet > 0) {
                    cache = if (inputLen - offSet > blockSize) {
                        cipher.doFinal(encryptedData, offSet, blockSize)
                    } else {
                        cipher.doFinal(encryptedData, offSet, inputLen - offSet)
                    }
                    out.write(cache, 0, cache.size)
                    i++
                    offSet = i * blockSize
                }
                out.toByteArray()
            }
            decryptedData.toString(Charsets.UTF_8)
        } catch (e: Exception) {
            e.throwIfNecessary()
            throw RsaException(
                "EncodeContent = $content,charset = $charset",
                e
            )
        }
    }
}