package com.labijie.application.crypto

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import java.security.MessageDigest
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


object HashUtils {
    private const val SIGN_HMAC_SHA256_ALGORITHMS = "HmacSHA256"

    fun genHmacSha256Key(): String {
        return genMD5Key()
    }

    fun genMD5Key(): String {
        return UUID.randomUUID().toString().replace("-",  "").lowercase()
    }

    fun signHmacSha256(params: Map<String, String>, key: String): String {
        return try {
            val secretKey = SecretKeySpec(key.toByteArray(Charsets.UTF_8), SIGN_HMAC_SHA256_ALGORITHMS)

            val mac = Mac.getInstance(SIGN_HMAC_SHA256_ALGORITHMS)
            mac.init(secretKey)

            val content = getSignContent(params).plus("&key=$key")
            val rawHmac = mac.doFinal(content.toByteArray())
            val hexBytes = Hex().encode(rawHmac)
            String(hexBytes, Charsets.UTF_8).lowercase()
        } catch (e: Exception) {
            throw RuntimeException()
        }
    }

    fun verifyHmacSha256(params: Map<String, String>, sign: String, key: String): Boolean {
        val verifySign = signHmacSha256(params, key)
        return sign == verifySign
    }

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

    fun signMD5(params: Map<String, String>, key: String): String {
        val content = getSignContent(params).plus("&key=$key")
        return DigestUtils.md5Hex(content).lowercase()
    }

    fun verifyMD5(params: Map<String, String>, sign: String, key: String): Boolean {
        val verifySign = signMD5(params, key)
        return sign == verifySign
    }


    /**
     * 利用java原生的类实现SHA256加密
     * @param str 加密后的报文
     * @return
     */
    fun sha256(str: String): String {
        val messageDigest: MessageDigest
        messageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(str.toByteArray(charset("UTF-8")))
        return byte2Hex(messageDigest.digest())
    }

    /**
     * 将byte转为16进制
     * @param bytes
     * @return
     */
    private fun byte2Hex(bytes: ByteArray): String {
        val stringBuffer = StringBuffer()
        for (i in bytes.indices) {
            val temp = Integer.toHexString(bytes[i].toInt() and 0xFF)
            if (temp.length == 1) {
                //1得到一位的进行补0操作
                stringBuffer.append("0")
            }
            stringBuffer.append(temp)
        }

        return stringBuffer.toString()
    }
}