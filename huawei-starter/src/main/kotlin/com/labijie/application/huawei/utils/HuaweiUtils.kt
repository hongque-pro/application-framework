package com.labijie.application.huawei.utils

import com.labijie.application.BucketPolicy
import com.labijie.application.crypto.HashUtils
import com.labijie.application.exception.StoredObjectNotFoundException
import com.labijie.application.formUrlEncode
import com.labijie.application.huawei.model.HuaweiProperties
import com.labijie.application.huawei.model.HuaweiSmsResponse
import com.labijie.application.huawei.model.HuaweiSmsTemplateParam
import com.labijie.infra.json.JacksonHelper
import com.labijie.infra.utils.logger
import com.obs.services.ObsClient
import com.obs.services.model.HttpMethodEnum
import com.obs.services.model.TemporarySignatureRequest
import com.obs.services.model.TemporarySignatureResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.util.StreamUtils
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URI
import java.net.URL
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*


class HuaweiUtils(
    private val huaweiProperties: HuaweiProperties,
    private val restTemplate: RestTemplate
) {

    private fun wsse(): String {
        val nonce = UUID.randomUUID().toString()
        val created = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(Date())
        val passwordDigest = Base64.getEncoder().encodeToString(
            HashUtils.sha256("${nonce}${created}${huaweiProperties.smsSettings.appSecret}")
                .toByteArray(Charset.defaultCharset())
        )

        val result = "UsernameToken Username=\"${huaweiProperties.smsSettings.appKey}\"," +
                "PasswordDigest=\"${passwordDigest}\"," +
                "Nonce=\"${nonce}\",Created=\"${created}\""

        return result
    }

    fun sendSmsMessage(phoneNumber: String, template: String, templateParam: HuaweiSmsTemplateParam?) {
        templateParam ?: throw NullPointerException()

        val url = "${huaweiProperties.smsSettings.apiServer}/sms/batchSendSms/v1"
        val headers = HttpHeaders()
        val body = mutableMapOf<String, String>().apply {
            put("from", templateParam.sender)
            put("to", phoneNumber)
            put("templateId", template)
            if (templateParam.templateParas.isNotEmpty()) {
                put("templateParas", JacksonHelper.serializeAsString(templateParam.templateParas))
            }
            if (!templateParam.signature.isNullOrBlank()) {
                put("signature", templateParam.signature.orEmpty())
            }
        }

        headers.add("Content-Type", "application/x-www-form-urlencoded")
        headers.add("Authorization", "WSSE realm=\"SDP\",profile=\"UsernameToken\",type=\"Appkey\"")
        headers.add("X-WSSE", wsse())

        val request: RequestEntity<String> = RequestEntity(body.formUrlEncode(true), headers, HttpMethod.POST, URI(url))
        try {
            val responseEntity = restTemplate.exchange(request, HuaweiSmsResponse::class.java)
            if (responseEntity.statusCode != HttpStatus.OK) {
                logger.warn("发送短信失败: {}", JacksonHelper.serializeAsString(responseEntity.body ?: ""))
            }
        } catch (e: HttpClientErrorException.BadRequest) {
            logger.error("发送短信失败:{}", e.responseBodyAsString)
        }
    }


    private fun getObsClient(): ObsClient {
        return ObsClient(
            huaweiProperties.obsSettings.ak,
            huaweiProperties.obsSettings.sk,
            huaweiProperties.obsSettings.endPoint
        )
    }

    fun existObject(key: String, throwIfNotExisted: Boolean, bucketPolicy: BucketPolicy): Boolean {
        val client = getObsClient()
        val result = client.doesObjectExist(huaweiProperties.obsSettings.bucketName, getRealKey(key, bucketPolicy))

        if (throwIfNotExisted && !result) {
            throw StoredObjectNotFoundException()
        }

        return result
    }

    fun deleteObject(key: String, bucketPolicy: BucketPolicy): Boolean {
        val client = getObsClient()
        val deleteObjectResult =
            client.deleteObject(huaweiProperties.obsSettings.bucketName, getRealKey(key, bucketPolicy))

        return deleteObjectResult.isDeleteMarker
    }

    fun generateObjectUrl(key: String, bucketPolicy: BucketPolicy): URL {
        val client = getObsClient()

        if (bucketPolicy == BucketPolicy.PRIVATE) {
            val request = TemporarySignatureRequest(HttpMethodEnum.GET, huaweiProperties.obsSettings.expireSeconds)
            request.bucketName = huaweiProperties.obsSettings.bucketName
            request.objectKey = getRealKey(key, bucketPolicy)

            val response: TemporarySignatureResponse = client.createTemporarySignature(request)
            return URL(response.signedUrl)
        } else {
            return URL("https://${huaweiProperties.obsSettings.domain}/${getRealKey(key, bucketPolicy)}")
        }
    }

    private fun getDir(bucketPolicy: BucketPolicy) =
        if (bucketPolicy == BucketPolicy.PUBLIC) huaweiProperties.obsSettings.publicDir else huaweiProperties.obsSettings.privateDir

    private fun getRealKey(key: String, bucketPolicy: BucketPolicy): String {
        if (key.startsWith("/")) {
            return getDir(bucketPolicy) + key
        } else {
            return getDir(bucketPolicy) + "/" + key
        }
    }

    fun uploadObject(key: String, stream: InputStream, bucketPolicy: BucketPolicy) {
        val client = getObsClient()

        client.putObject(huaweiProperties.obsSettings.bucketName, getRealKey(key, bucketPolicy), stream)
//        client.setObjectAcl(huaweiProperties.obsSettings.bucketName, key, if(bucketPolicy == BucketPolicy.PRIVATE) AccessControlList.REST_CANNED_PRIVATE else AccessControlList.REST_CANNED_PUBLIC_READ)
    }

    fun getObject(key: String, bucketPolicy: BucketPolicy): ByteArray {
        val client = getObsClient()
        val obsObject = client.getObject(huaweiProperties.obsSettings.bucketName, getRealKey(key, bucketPolicy))
        val stream = ByteArrayOutputStream()
        try {
            if (obsObject == null || obsObject.objectContent == null) {
                return byteArrayOf()
            }

            StreamUtils.copy(obsObject.objectContent, stream)

            return stream.toByteArray()
        } finally {
            try {
                stream.close()
            } catch (e: Exception) {
            }
        }
    }
}
