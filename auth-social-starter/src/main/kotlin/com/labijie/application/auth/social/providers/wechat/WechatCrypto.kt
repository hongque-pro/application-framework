package com.labijie.application.auth.social.providers.wechat

import com.labijie.application.crypto.AesUtils
import com.labijie.application.crypto.RsaUtils
import com.labijie.infra.utils.throwIfNecessary
import org.apache.commons.codec.digest.DigestUtils
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.lang.Exception
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
object WechatCrypto {

    private const val KEY_ALGORITHM = "AES"
    private const val AES_ALGORITHM = "AES/CBC/PKCS7Padding"

    init {
        AesUtils.ensureBouncyCastleProvider()
    }

    private fun getKey(key: ByteArray, keyLength: Int = 16): SecretKeySpec {
        var keyBytes = key
        val keySize = keyBytes.size
        if (keySize % keyLength != 0) {
            val groups: Int = keyBytes.size / keyLength + if (keySize % keyLength != 0) 1 else 0
            val temp = ByteArray(groups * keyLength)
            Arrays.fill(temp, 0.toByte())
            System.arraycopy(keyBytes, 0, temp, 0, keyBytes.size)
            keyBytes = temp
        }

        return SecretKeySpec(keyBytes, KEY_ALGORITHM)
    }


    private fun decryptAes(encryptedData: ByteArray, keyBytes: ByteArray, ivs: ByteArray): ByteArray? {
        val key = getKey(keyBytes)

        val cipher = Cipher.getInstance(AES_ALGORITHM, "BC").apply {
            this.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(ivs))
        }

        return cipher.doFinal(encryptedData)
    }

    fun decryptAes(encryptedData: String, sessionKey: String, ivs: String): String {
        val data = Base64.getDecoder().decode(encryptedData)
        val key = Base64.getDecoder().decode(sessionKey)
        val iv = Base64.getDecoder().decode(ivs)

        val plain = this.decryptAes(data, key, iv)
        return plain?.toString(Charsets.UTF_8) ?: ""
    }

    fun verifySha1(data: String, sessionKey:String, sign: String): Boolean{
        return DigestUtils.sha1Hex(data + sessionKey) == sign
    }
}