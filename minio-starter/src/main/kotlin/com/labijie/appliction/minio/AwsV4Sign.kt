package com.labijie.appliction.minio

import java.io.UnsupportedEncodingException
import java.math.BigInteger
import java.net.URLEncoder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.HashMap


/**
 * AWS V4 签名处理工具
 *
 * 参考链接：https://docs.aws.amazon.com/zh_cn/general/latest/gr/sigv4_signing.html
 */
class AWSV4Auth private constructor() {

    class Builder(internal val accessKeyID: String, internal val secretAccessKey: String) {
        internal var regionName: String? = null
        internal var serviceName: String? = null
        internal var httpMethodName: String? = null
        internal var canonicalURI: String? = null
        internal var queryParametes: TreeMap<String, String>? = null
        internal var awsHeaders: TreeMap<String, String?>? = null
        internal var payload: String? = null
        internal var debug = false
        fun regionName(regionName: String?): Builder {
            this.regionName = regionName
            return this
        }

        fun serviceName(serviceName: String?): Builder {
            this.serviceName = serviceName
            return this
        }

        fun httpMethodName(httpMethodName: String?): Builder {
            this.httpMethodName = httpMethodName
            return this
        }

        fun canonicalURI(canonicalURI: String?): Builder {
            this.canonicalURI = canonicalURI
            return this
        }

        fun queryParametes(queryParametes: TreeMap<String, String>?): Builder {
            this.queryParametes = queryParametes
            return this
        }

        fun awsHeaders(awsHeaders: TreeMap<String, String?>?): Builder {
            this.awsHeaders = awsHeaders
            return this
        }

        fun payload(payload: String?): Builder {
            this.payload = payload
            return this
        }

        fun debug(): Builder {
            debug = true
            return this
        }

        fun build(): AWSV4Auth {
            return AWSV4Auth(this)
        }
    }

    private var accessKeyID: String? = null
    private var secretAccessKey: String? = null
    private var regionName: String? = null
    private var serviceName: String? = null
    private var httpMethodName: String? = null
    private var canonicalURI: String? = null
    private var queryParametes: TreeMap<String, String>? = null
    private var awsHeaders: TreeMap<String, String?>? = null
    private var payload: String? = null
    private var debug = false

    /* Other variables */
    private val HMACAlgorithm = "AWS4-HMAC-SHA256"
    private val aws4Request = "aws4_request"
    private var strSignedHeader: String? = null
    private var xAmzDate: String? = null
    private var currentDate: String? = null

    private constructor(builder: Builder) : this() {
        accessKeyID = builder.accessKeyID
        secretAccessKey = builder.secretAccessKey
        regionName = builder.regionName
        serviceName = builder.serviceName
        httpMethodName = builder.httpMethodName
        canonicalURI = builder.canonicalURI
        queryParametes = builder.queryParametes
        awsHeaders = builder.awsHeaders
        payload = builder.payload
        debug = builder.debug

        /* Get current timestamp value.(UTC) */xAmzDate = timeStamp
        currentDate = date
    }

    /**
     * 任务 1：针对签名版本 4 创建规范请求
     *
     * @return
     */
    private fun prepareCanonicalRequest(): String {
        val canonicalURL = StringBuilder("")

        /* Step 1.1 以HTTP方法(GET, PUT, POST, etc.)开头, 然后换行. */canonicalURL.append(httpMethodName).append("\n")

        /* Step 1.2 添加URI参数，换行. */canonicalURI =
            if (canonicalURI == null || canonicalURI!!.trim { it <= ' ' }.isEmpty()) "/" else canonicalURI
        canonicalURL.append(canonicalURI).append("\n")

        /* Step 1.3 添加查询参数，换行. */
        val queryString = StringBuilder("")
        if (queryParametes != null && !queryParametes!!.isEmpty()) {
            for ((key, value) in queryParametes!!) {
                queryString.append(key).append("=").append(encodeParameter(value)).append("&")
            }
            queryString.deleteCharAt(queryString.lastIndexOf("&"))
            queryString.append("\n")
        } else {
            queryString.append("\n")
        }
        canonicalURL.append(queryString)

        /* Step 1.4 添加headers, 每个header都需要换行. */
        val signedHeaders = StringBuilder("")
        if (awsHeaders != null && !awsHeaders!!.isEmpty()) {
            for ((key, value) in awsHeaders!!) {
                signedHeaders.append(key).append(";")
                canonicalURL.append(key).append(":").append(value).append("\n")
            }
            canonicalURL.append("\n")
        } else {
            canonicalURL.append("\n")
        }

        /* Step 1.5 添加签名的headers并换行. */strSignedHeader =
            signedHeaders.substring(0, signedHeaders.length - 1) // 删掉最后的 ";"
        canonicalURL.append(strSignedHeader).append("\n")

        /* Step 1.6 对HTTP或HTTPS的body进行SHA256处理. */payload = if (payload == null) "" else payload
        canonicalURL.append(generateHex(payload))
        if (debug) {
            println("##Canonical Request:\n$canonicalURL")
        }
        return canonicalURL.toString()
    }

    /**
     * 任务 2：创建签名版本 4 的待签字符串
     *
     * @param canonicalURL
     * @return
     */
    private fun prepareStringToSign(canonicalURL: String): String {
        var stringToSign: String? = ""

        /* Step 2.1 以算法名称开头，并换行. */stringToSign = """
    $HMACAlgorithm
    
    """.trimIndent()

        /* Step 2.2 添加日期，并换行. */stringToSign += """
    $xAmzDate
    
    """.trimIndent()

        /* Step 2.3 添加认证范围，并换行. */stringToSign += "$currentDate/$regionName/$serviceName/$aws4Request\n"

        /* Step 2.4 添加任务1返回的规范URL哈希处理结果，然后换行. */stringToSign += generateHex(canonicalURL)
        if (debug) {
            println("##String to sign:\n$stringToSign")
        }
        return stringToSign
    }

    /**
     * 任务 3：为 AWS Signature 版本 4 计算签名
     *
     * @param stringToSign
     * @return
     */
    private fun calculateSignature(stringToSign: String): String? {
        try {
            /* Step 3.1 生成签名的key */
            val signatureKey = getSignatureKey(secretAccessKey, currentDate, regionName, serviceName)

            /* Step 3.2 计算签名. */
            val signature = HmacSHA256(signatureKey, stringToSign)

            /* Step 3.2.1 对签名编码处理 */
            return bytesToHex(signature)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }/* 执行任务 1: 创建aws v4签名的规范请求字符串. */

    /* 执行任务 2: 创建用来认证的字符串 4. */

    /* 执行任务 3: 计算签名. */
    /**
     * 任务 4：将签名信息添加到请求并返回headers
     *
     * @return
     */
    val headers: Map<String, String?>?
        get() {
            awsHeaders!!["x-amz-date"] = xAmzDate

            /* 执行任务 1: 创建aws v4签名的规范请求字符串. */
            val canonicalURL = prepareCanonicalRequest()

            /* 执行任务 2: 创建用来认证的字符串 4. */
            val stringToSign = prepareStringToSign(canonicalURL)

            /* 执行任务 3: 计算签名. */
            val signature = calculateSignature(stringToSign)
            return if (signature != null) {
                val header: MutableMap<String, String?> = HashMap(0)
                header["x-amz-date"] = xAmzDate
                header["Authorization"] = buildAuthorizationString(signature)
                if (debug) {
                    println("##Signature:\n$signature")
                    println("##Header:")
                    for ((key, value) in header) {
                        println("$key = $value")
                    }
                    println("================================")
                }
                header
            } else {
                if (debug) {
                    println("##Signature:\n$signature")
                }
                null
            }
        }

    /**
     * 连接前几步处理的字符串生成Authorization header值.
     *
     * @param strSignature
     * @return
     */
    private fun buildAuthorizationString(strSignature: String): String {
        return (HMACAlgorithm + " "
                + "Credential=" + accessKeyID + "/" + date + "/" + regionName + "/" + serviceName + "/" + aws4Request + ", "
                + "SignedHeaders=" + strSignedHeader + ", "
                + "Signature=" + strSignature)
    }

    /**
     * 将字符串16进制化.
     *
     * @param data
     * @return
     */
    private fun generateHex(data: String?): String? {
        val messageDigest: MessageDigest
        try {
            messageDigest = MessageDigest.getInstance("SHA-256")
            messageDigest.update(data!!.toByteArray(charset("UTF-8")))
            val digest: ByteArray = messageDigest.digest()
            return String.format("%064x", BigInteger(1, digest))
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 以给定的key应用HmacSHA256算法处理数据.
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     * @reference:
     * http://docs.aws.amazon.com/general/latest/gr/signature-v4-examples.html#signature-v4-examples-java
     */
    @Throws(Exception::class)
    private fun HmacSHA256(key: ByteArray, data: String?): ByteArray {
        val algorithm = "HmacSHA256"
        val mac: Mac = Mac.getInstance(algorithm)
        mac.init(SecretKeySpec(key, algorithm))
        return mac.doFinal(data!!.toByteArray(charset("UTF8")))
    }

    /**
     * 生成AWS 签名
     *
     * @param key
     * @param date
     * @param regionName
     * @param serviceName
     * @return
     * @throws Exception
     * @reference
     * http://docs.aws.amazon.com/general/latest/gr/signature-v4-examples.html#signature-v4-examples-java
     */
    @Throws(Exception::class)
    private fun getSignatureKey(
        key: String?,
        date: String?,
        regionName: String?,
        serviceName: String?
    ): ByteArray {
        val kSecret = "AWS4$key".toByteArray(charset("UTF8"))
        val kDate = HmacSHA256(kSecret, date)
        val kRegion = HmacSHA256(kDate, regionName)
        val kService = HmacSHA256(kRegion, serviceName)
        return HmacSHA256(kService, aws4Request)
    }

    /**
     * 将字节数组转换为16进制字符串
     *
     * @param bytes
     * @return
     */
    private fun bytesToHex(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        for (j in bytes.indices) {
            val v: Int = bytes[j].toInt() and 0xFF
            hexChars[j * 2] = hexArray[v ushr 4]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars).toLowerCase()
    }//server timezone

    /**
     * 获取yyyyMMdd'T'HHmmss'Z'格式的当前时间
     *
     * @return
     */
    private val timeStamp: String
        private get() {
            val dateFormat: DateFormat = SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'")
            dateFormat.timeZone = TimeZone.getTimeZone("UTC") //server timezone
            return dateFormat.format(Date())
        }//server timezone

    /**
     * 获取yyyyMMdd格式的当前日期
     *
     * @return
     */
    private val date: String
        private get() {
            val dateFormat: DateFormat = SimpleDateFormat("yyyyMMdd")
            dateFormat.timeZone = TimeZone.getTimeZone("UTC") //server timezone
            
            return dateFormat.format(Date())
        }

    /**
     * UTF-8编码
     * @param param
     * @return
     */
    private fun encodeParameter(param: String): String {
        return try {
            URLEncoder.encode(param, "UTF-8")
        } catch (e: Exception) {
            URLEncoder.encode(param)
        }
    }

    companion object {
        protected val hexArray = "0123456789ABCDEF".toCharArray()
    }
}