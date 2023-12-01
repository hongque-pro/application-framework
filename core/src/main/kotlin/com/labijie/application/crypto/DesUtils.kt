package com.labijie.application.crypto

import com.labijie.application.exception.InvalidSecurityStampException
import com.labijie.application.toUTF8StringOrEmpty
import com.labijie.infra.utils.ifNullOrBlank
import com.labijie.infra.utils.throwIfNecessary
import java.lang.Exception
import java.security.MessageDigest
import java.time.Duration
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-09
 */
object DesUtils {
    private const val KEY_ALGORITHM = "DESede"
    private const val DEFAULT_CIPHER_ALGORITHM = "DESede/CBC/PKCS5Padding" //默认的加密算法

    var key: String = "labijie@5f98H*^hsff%dfs\$r344&df8543*er"

    private fun getSecretKey(key: String): SecretKeySpec { //返回生成指定算法密钥生成器的 KeyGenerator 对象
        val md = MessageDigest.getInstance("md5")
        val digestOfPassword = md.digest(
            key.toByteArray(charset("utf-8"))
        )
        //DESede
        val keyBytes: ByteArray = digestOfPassword.copyOf(24)

        var j = 0
        var k = 16
        while (j < 8) {
            keyBytes[k] = keyBytes[j]
            k+= 1
            j += 1
        }

        //生成一个密钥
        val secretKey: SecretKey = SecretKeySpec(keyBytes,
            KEY_ALGORITHM
        )
        return SecretKeySpec(secretKey.encoded,
            KEY_ALGORITHM
        ) // 转换为DESede专用密钥
    }

    /**
     * DESede 加密操作
     *
     * @param content 待加密内容
     * @param key 加密密钥
     * @return 返回Base64转码后的加密数据
     */
    fun encrypt(content: String, key: String? = null): String {
        try {
            val cipher: Cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM) // 创建密码器
            val byteContent = content.toByteArray(Charsets.UTF_8)

            val skey =
                getSecretKey(key.ifNullOrBlank { DesUtils.key })
            val iv = IvParameterSpec(ByteArray(8))
            cipher.init(Cipher.ENCRYPT_MODE, skey, iv) // 初始化为加密模式的密码器
            val result: ByteArray = cipher.doFinal(byteContent) // 加密
            return Base64.getEncoder().encodeToString(result)
        }catch (e: Exception){
            e.throwIfNecessary()
            throw DesException( "AESContent = $content", e)
        }
    }

    /**
     * DESede 解密操作
     *
     * @param content
     * @param key
     * @return
     */
    fun decrypt(content: String, key: String? = null, throwIfBadData:Boolean = false): String {
        return try {
            val cipher: Cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM)
            //使用密钥初始化，设置为解密模式
            val skey =
                getSecretKey(key.ifNullOrBlank { DesUtils.key })
            val iv = IvParameterSpec(ByteArray(8))
            cipher.init(Cipher.DECRYPT_MODE, skey, iv)
            //执行操作
            val result: ByteArray = cipher.doFinal(Base64.getDecoder().decode(content))
            result.toUTF8StringOrEmpty()
        }catch (e: Exception){
            if(throwIfBadData){
                throw DesException( "AESContent = $content", e)
            }
            ""
        }
    }

    fun generateToken(modifier:String, expired:Duration, key: String? = null): String {
        if(modifier.contains('|')){
            throw IllegalArgumentException("Modifier cant contains char: '|'.")
        }
        val str = "$modifier|${System.currentTimeMillis() + expired.toMillis()}"
        return encrypt(str, key)
    }

    fun verifyToken(token:String, modifier:String, key: String? = null, throwIfInvalid: Boolean = false) : Boolean{
        val content =
            decrypt(token, key, throwIfBadData = false)
        if(content.isNotBlank()) {
            val segments = content.split("|").filter { it.isNotBlank() }
            if (segments.size == 2 && segments[0] == modifier) {
                val time = segments[1].toLongOrNull()
                if (time != null) {
                    return System.currentTimeMillis() <= time
                }
            }
        }
        if(throwIfInvalid){
            throw InvalidSecurityStampException()
        }
        return false
    }
}