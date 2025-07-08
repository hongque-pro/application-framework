package com.labijie.application.thridparty.aws

import com.labijie.application.QueryStringEncoder
import com.labijie.infra.utils.ifNullOrBlank
import org.slf4j.LoggerFactory
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.web.util.UriUtils
import java.math.BigInteger
import java.net.URLEncoder
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


/**
 * AWS V4 签名处理工具
 *
 * 参考链接：https://docs.aws.amazon.com/zh_cn/general/latest/gr/sigv4_signing.html
 */
class AwsV4Signer private constructor() {

    companion object {
        private val hexArray = "0123456789ABCDEF".toCharArray()
        private val logger by lazy {
            LoggerFactory.getLogger(AwsV4Signer::class.java)
        }

//        fun UriEncode(input: CharSequence, encodeSlash: Boolean): String? {
//            val result = java.lang.StringBuilder()
//            input.forEach {
//                ch->
//                if (ch in 'A'..'Z' || ch in 'a'..'z' || ch in '0'..'9' || ch == '_' || ch == '-' || ch == '~' || ch == '.') {
//                    result.append(ch)
//                } else if (ch == '/') {
//                    result.append(if (encodeSlash) "%2F" else ch)
//                } else {
//                    HexUtils.toHexString()
//                    result.append(toHexUTF8(ch))
//                }
//            }
//            return result.toString()
//        }

        fun TreeMap<String, SortedSet<String>>.putSingle(name: String, value: String){
            val set = this.getOrPut(name) { TreeSet() }
            set.add(value)
        }

    }

    internal class Ns(val origin: String) {
        override fun toString(): String {
            return origin.lowercase()
        }
    }

    internal object NsComparator : Comparator<Ns> {
        override fun compare(o1: Ns?, o2: Ns?): Int {
            return o1?.toString()?.compareTo((o2?.toString().orEmpty())) ?: -1
        }

    }

    class Builder(internal val accessKeyID: String, internal val secretAccessKey: String) {
        internal var regionName: String = "us-east-1"
        internal var serviceName: String? = null
        internal var httpMethodName: String? = null
        internal var canonicalURI: String = "/"
        internal var queryParameters: TreeMap<String, SortedSet<String>> = TreeMap()
        internal var awsHeaders = TreeMap<Ns, String>(NsComparator)
        internal var payload: String? = null
        internal var queryStringUsed: Boolean = false
        fun regionName(regionName: String?): Builder {
            this.regionName = regionName ?: "us-east-1"
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

        fun uri(uri: String): Builder {
            val uriObj = UriComponentsBuilder.fromUriString(uri).build()
            this.canonicalURI = (uriObj.path?.removePrefix("/")).ifNullOrBlank { "/" }
            val host = uriObj.host ?: throw IllegalArgumentException("no host in uri: '$uri'")
            this.awsHeaders[Ns("Host")] = if (uriObj.port < 0) host else "$host:${uriObj.port}"

            val self = this
            uriObj.queryParams.forEach { (name, values) ->
                val n = name.removePrefix("/").removePrefix("?")
                val set = self.queryParameters.getOrPut(n) { TreeSet() }
                if (!values.isNullOrEmpty()) {
                    set.addAll(values)
                }
            }
            return this
        }

        fun header(name: String, value: String): Builder {
            this.awsHeaders[Ns(name)] = value
            return this
        }

        fun payload(payload: String): Builder {
            if (payload.isNotBlank()) {
                this.payload = payload
            }
            return this
        }

        fun useQueryString(enabled: Boolean): Builder {
            this.queryStringUsed = enabled
            return this
        }


        fun build(): AwsV4Signer {
            return AwsV4Signer(this)
        }
    }

    private var accessKeyID: String? = null
    private var secretAccessKey: String? = null
    private var regionName: String? = null
    private var serviceName: String? = null
    private var httpMethodName: String? = null
    private var canonicalURI: String = "/"
    private lateinit var queryParametes: TreeMap<String, SortedSet<String>>
    private lateinit var awsHeaders: TreeMap<Ns, String>
    private var payload: String? = null

    /* Other variables */
    private val HMACAlgorithm = "AWS4-HMAC-SHA256"
    private val aws4Request = "aws4_request"
    private var strSignedHeader: String? = null

    private var credentialScope: String = ""


    /**
     * 获取yyyyMMdd格式的当前日期
     *
     * @return
     */
    private var date: String? = null

    /**
     * 获取yyyyMMdd'T'HHmmss'Z'格式的当前时间
     *
     * @return
     */
    private var xAmzDate: String? = null

    private constructor(builder: Builder) : this() {
        accessKeyID = builder.accessKeyID
        secretAccessKey = builder.secretAccessKey
        regionName = builder.regionName
        serviceName = builder.serviceName
        httpMethodName = builder.httpMethodName
        canonicalURI = builder.canonicalURI
        queryParametes = builder.queryParameters
        awsHeaders = builder.awsHeaders
        payload = builder.payload
        queryStringUsed = builder.queryStringUsed

        val now = LocalDateTime.now()
        useAmzDate(now)

    }

    fun useAmzDateString(datetime: String): AwsV4Signer {
        val format = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")

        val ldt = LocalDateTime.parse(datetime, format)

        return useAmzDate(ldt)
    }

    fun useAmzDate(datetime: LocalDateTime): AwsV4Signer {
        val yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneOffset.UTC)
        val amzDate = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").withZone(ZoneOffset.UTC)
        xAmzDate = datetime.format(amzDate)
        date = datetime.format(yyyyMMdd)
        return this
    }

    /**
     * 任务 1：针对签名版本 4 创建规范请求
     *
     * @return
     */
    private fun prepareCanonicalRequest(): String {
        val canonicalURL = StringBuilder()

        /* Step 1.1 以HTTP方法(GET, PUT, POST, etc.)开头, 然后换行. */canonicalURL.append(httpMethodName).append("\n")

        /* Step 1.2 添加URI参数，换行. */
        canonicalURI = if (canonicalURI.isBlank()) "/" else canonicalURI

        canonicalURL.append(canonicalURI).append("\n")

        /* Step 1.3 添加查询参数，换行. */
        val queryString = StringBuilder()
        if (queryParametes.isNotEmpty()) {
            val qs = queryParametes.flatMap {
                val key = UriUtils.encode(it.key, Charsets.UTF_8)
                it.value.map { v ->
                    val value = UriUtils.encode(v, Charsets.UTF_8)
                    "$key=$value"
                }
            }.joinToString("&")
            queryString.append(qs)
        }
        queryString.append("\n")
        canonicalURL.append(queryString)

        /* Step 1.4 添加headers, 每个header都需要换行. */
        if (awsHeaders.isNotEmpty()) {
            canonicalURL.append(awsHeaders.toList().joinToString("\n") {
                val value = it.second.trim().replace(Regex(" +"), " ")
                "${it.first}:${value}"
            })
                .append("\n")
        }
        canonicalURL.append("\n")

        /* Step 1.5 添加签名的headers并换行. */
        if (strSignedHeader != null) {
            canonicalURL.append(strSignedHeader)
        }
        canonicalURL.append("\n")

        /* Step 1.6 对HTTP或HTTPS的body进行SHA256处理. */
        payload = if (payload == null) "" else payload
        canonicalURL.append(generateHex(payload))
        if (logger.isDebugEnabled) {
            logger.debug("\n##Canonical Request:\n$canonicalURL\n\n${generateHex(canonicalURL.toString())}")

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

        val stringToSign = StringBuilder()

        /* Step 2.1 以算法名称开头，并换行. */
        stringToSign.append(HMACAlgorithm + "\n")

        /* Step 2.2 添加日期，并换行. */
        stringToSign.append(xAmzDate + "\n")

        /* Step 2.3 添加认证范围，并换行. */
        credentialScope = "$date/$regionName/$serviceName/$aws4Request"
        stringToSign.append("$credentialScope\n")

        /* Step 2.4 添加任务1返回的规范URL哈希处理结果，然后换行. */
        stringToSign.append(generateHex(canonicalURL))

        if (logger.isDebugEnabled) {
            logger.debug("\n##String to sign:${System.lineSeparator()}$stringToSign")
        }

        return stringToSign.toString()


    }

    /**
     * 任务 3：为 AWS Signature 版本 4 计算签名
     *
     * @param stringToSign
     * @return
     */
    private fun calculateSignature(stringToSign: String): String {

        /* Step 3.1 生成签名的key */
        val signatureKey = getSignatureKey(secretAccessKey, date, regionName, serviceName)

        /* Step 3.2 计算签名. */
        val signature = hmacSHA256(signatureKey, stringToSign)

        /* Step 3.2.1 对签名编码处理 */
        return bytesToHex(signature)
    }

    /* 执行任务 1: 创建aws v4签名的规范请求字符串. */

    /* 执行任务 2: 创建用来认证的字符串 4. */

    /* 执行任务 3: 计算签名. */
    /**
     * 任务 4：将签名信息添加到请求并返回headers
     *
     * @return
     */
    fun headers(): Map<String, String?>? {
        val signature = sign()
        return if (signature.isNotBlank()) {
            val header: MutableMap<String, String?> = mutableMapOf(*this.awsHeaders.map {
                it.key.origin to it.value
            }.toTypedArray())
            header["Authorization"] = buildAuthorizationHeaderValue(signature)
            if (logger.isDebugEnabled) {
                val logMsg = StringBuilder().apply {
                    appendLine("##Signature:\n$signature")
                    appendLine("##Header:")
                    for ((key, value) in header) {
                        appendLine("$key = $value")
                    }
                    appendLine("================================")
                }.toString()
                logger.debug(logMsg)
            }
            header
        } else {
            if (logger.isDebugEnabled) {
                logger.debug("##Signature:\n$signature")
            }
            null
        }
    }

    private var queryStringUsed = false

    fun sign(): String {
        if (this.xAmzDate == null || this.date == null) {
            this.useAmzDate(LocalDateTime.now())
        }

        awsHeaders[Ns("X-Amz-Date")] = this.xAmzDate ?: throw RuntimeException("xAmzDate is null")

        strSignedHeader = if (awsHeaders.isNotEmpty()) awsHeaders.keys.joinToString(";") else null

        if (queryStringUsed) {
            queryParametes.putSingle("X-Amz-Algorithm", HMACAlgorithm)
            queryParametes.putSingle("X-Amz-Credential", "$accessKeyID/$credentialScope")
            queryParametes.putSingle("X-Amz-Date", xAmzDate ?: throw RuntimeException("xAmzDate is null"))
            queryParametes.putSingle("X-Amz-SignedHeaders",strSignedHeader.orEmpty())
            queryParametes.putSingle("X-Amz-Expires", "60")
        }
        val canonicalURL = prepareCanonicalRequest()
        val stringToSign = prepareStringToSign(canonicalURL)
        return calculateSignature(stringToSign)
    }

    fun queryParams(): HttpHeadObject<MutableMap<String, String>> {
        val signature = sign()


        val values = mutableMapOf<String, String>()
        values["X-Amz-Algorithm"] = HMACAlgorithm
        values["X-Amz-Credential"] = "$accessKeyID/$credentialScope"
        values["X-Amz-Date"] = xAmzDate ?: throw RuntimeException("xAmzDate is null")
        values["X-Amz-SignedHeaders"] = strSignedHeader.orEmpty()
        values["X-Amz-Expires"] = "60"
        values["X-Amz-Signature"] = signature

        return HttpHeadObject(values, this.awsHeaders.map { it.key.origin to it.value }.toMap())
    }

    /**
     * 生成 query string 值.
     *
     */
    fun queryString(): HttpHeadObject<String> {
        val params = queryParams()

        val qs = QueryStringEncoder(null).apply {
            params.data.forEach {
                this.addParam(it.key, it.value)
            }
        }.toString().removePrefix("?")

        return HttpHeadObject(qs, this.awsHeaders.map { it.key.origin to it.value }.toMap())
    }

    /**
     * 生成 Authorization header 值.
     * Authorization: algorithm Credential=access key ID/credential scope, SignedHeaders=SignedHeaders, Signature=signature
     * @param strSignature
     * @return
     */
    private fun buildAuthorizationHeaderValue(strSignature: String): String {
        return "$HMACAlgorithm Credential=$accessKeyID/$credentialScope, SignedHeaders=$strSignedHeader, Signature=$strSignature"
//        return (HMACAlgorithm + " "
//                + "Credential=" + accessKeyID + "/" + date + "/" + regionName + "/" + serviceName + "/" + aws4Request + ", "
//                + "SignedHeaders=" + strSignedHeader + ", "
//                + "Signature=" + strSignature)
    }

    /**
     * 将字符串16进制化.
     *
     * @param data
     * @return
     */
    private fun generateHex(data: String?): String? {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(data!!.toByteArray(Charsets.UTF_8))
        val digest: ByteArray = messageDigest.digest()
        return String.format("%064x", BigInteger(1, digest))
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
    private fun hmacSHA256(key: ByteArray, data: String?): ByteArray {
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
        val kDate = hmacSHA256(kSecret, date)
        val kRegion = hmacSHA256(kDate, regionName)
        val kService = hmacSHA256(kRegion, serviceName)
        return hmacSHA256(kService, aws4Request)
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
        return String(hexChars).lowercase()
    }//server timezone


    /**
     * UTF-8编码
     * @param param
     * @return
     */
    private fun encodeParameter(param: String): String {
        return try {
            URLEncoder.encode(param, "UTF-8")
        } catch (e: Exception) {
            URLEncoder.encode(param, Charsets.UTF_8.name())
        }
    }


}