package com.labijie.application.crypto

import com.labijie.infra.utils.throwIfNecessary
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.nio.charset.Charset
import java.security.GeneralSecurityException
import java.security.Security
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-14
 */
object AesUtils {

    private var initialized = false

    fun ensureBouncyCastleProvider() {
        if(!initialized) {
            try {
                Security.addProvider(BouncyCastleProvider())
            } catch (e: java.lang.Exception) {
                e.throwIfNecessary()
            }
            initialized = true
        }
    }

    private const val AES_ALG = "AES"
    /**
     * AES算法
     */
    private const val AES_CBC_PCK_ALG = "AES/CBC/PKCS5Padding"
    private val AES_IV = initIv()

    val ecbPKCS7Padding by lazy {
        AesEcbPKCS7Padding()
    }

    /**
     * AES加密
     *
     * @param content
     * @param aesKey
     * @param charset
     * @return
     */
    fun encrypt(content: String, aesKey: String, charset: Charset = Charsets.UTF_8): String {
        return try {
            val cipher: Cipher = Cipher.getInstance(AES_CBC_PCK_ALG)
            val iv = IvParameterSpec(AES_IV)
            cipher.init(
                Cipher.ENCRYPT_MODE,
                SecretKeySpec(Base64.getDecoder().decode(aesKey.toByteArray()),
                    AES_ALG
                ), iv
            )
            val encryptBytes: ByteArray = cipher.doFinal(content.toByteArray(charset))
            Base64.getEncoder().encodeToString(encryptBytes)
        } catch (e: Exception) {
            e.throwIfNecessary()
            throw AesException(
                "AESContent = $content; charset = $charset",
                e
            )
        }
    }

    /**
     * AES解密
     *
     * @param content
     * @param key
     * @param charset
     * @return
     */
    fun decrypt(content: String, key: String, charset: Charset = Charsets.UTF_8): String {
        return try {
            val cipher: Cipher = Cipher.getInstance(AES_CBC_PCK_ALG)
            val iv = IvParameterSpec(AES_IV)
            cipher.init(
                Cipher.DECRYPT_MODE, SecretKeySpec(
                    Base64.getDecoder().decode(key.toByteArray()),
                    AES_ALG
                ), iv
            )
            val cleanBytes: ByteArray = cipher.doFinal(Base64.getDecoder().decode(content.toByteArray()))
            cleanBytes.toString(charset)
        } catch (e: Exception) {
            e.throwIfNecessary()
            throw AesException(
                "AESContent = $content; charset = $charset",
                e
            )
        }
    }

    /**
     * 初始向量的方法, 全部为0. 这里的写法适合于其它算法,针对AES算法的话,IV值一定是128位的(16字节).
     *
     * @param fullAlg
     * @return
     * @throws GeneralSecurityException
     */
    private fun initIv(): ByteArray {
        return try {
            val cipher: Cipher = Cipher.getInstance(AES_CBC_PCK_ALG)
            val blockSize: Int = cipher.blockSize
            val iv = ByteArray(blockSize)
            for (i in 0 until blockSize) {
                iv[i] = 0
            }
            iv
        } catch (e: Exception) {
            val blockSize = 16
            val iv = ByteArray(blockSize)
            var i = 0
            while (i < blockSize) {
                iv[i] = 0
                ++i
            }
            iv
        }
    }
}