package com.labijie.application.aliyun.controller

import com.labijie.application.aliyun.AliyunUtils
import com.labijie.application.aliyun.OssPolicy
import com.labijie.application.aliyun.model.StsAccessToken
import com.labijie.application.aliyun.configuration.AliyunProperties
import com.labijie.application.aliyun.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid


/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-20
 */
@RestController
@Validated
@RequestMapping("/aliyun")
class CloudProviderController {

    companion object{
        @JvmStatic
        private val formatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC)
    }
    @Autowired
    private lateinit var aliyunUtils: AliyunUtils

    @Autowired
    private lateinit var config: AliyunProperties

    @GetMapping("/token")
    fun getToken(): StsAccessToken {
        val c=  this.aliyunUtils.assumeRole()
        val dateExp = ZonedDateTime.parse(c.expiration, formatter)
        val time =  dateExp.toInstant().toEpochMilli()
        return StsAccessToken(c.securityToken, c.accessKeySecret, c.accessKeyId, expiration =time)
    }

    @PostMapping("/oss/temp-url")
    fun generatePrivateUrl(@RequestBody key: ObjectKey): UrlResult {
        val u = aliyunUtils.oss.getObjectUrl(key.key, key.bucket ?: OssPolicy.PRIVATE).toString()
        return UrlResult(u)
    }

    @GetMapping("/oss/cnf")
    fun getOssConfig(): AliyunCnfResponse.OssConfig {
        val private = BucketConfig(
            config.oss.private.region,
            config.oss.private.bucket,
            config.oss.private.endpoint?.toString().orEmpty(),
            config.oss.private.customDomain)

        val public = BucketConfig(
            config.oss.public.region,
            config.oss.public.bucket,
            config.oss.public.endpoint?.toString().orEmpty(),
            config.oss.public.customDomain)

        return AliyunCnfResponse.OssConfig(private , public)
    }

    @GetMapping("/cnf")
    fun getConfiguration(): AliyunCnfResponse {
        val private = BucketConfig(
            config.oss.private.region,
            config.oss.private.bucket,
            config.oss.private.endpoint?.toString().orEmpty(),
            config.oss.private.customDomain)

        val public = BucketConfig(
            config.oss.public.region,
            config.oss.public.bucket,
            config.oss.public.endpoint?.toString().orEmpty(),
            config.oss.public.customDomain)

        val oss = AliyunCnfResponse.OssConfig(private , public)

        return AliyunCnfResponse().apply {
            this.oss = oss
            this.afs = config.afs
        }
    }

    fun HttpServletRequest.getRealIp(): String {
//        println(this.headerNames.asSequence().map {
//            "$it=${this.getHeader(it)}"
//        }.joinToString(System.lineSeparator()))
        return this.getHeader("X-Forwarded-For")
            ?: this.getHeader("X-Real-IP")
            ?: this.getHeader("WL-Proxy-Client-IP")
            ?: this.remoteAddr
    }

    @PostMapping("/afs/verify")
    fun afs(@RequestBody @Valid data : AfsRequest, request: HttpServletRequest): AfsResult {
        data.remoteIp = request.getRealIp()
        if(!config.afs.enabled){
            return AfsResult().apply {
                this.code = "000"
            }
        }
        return aliyunUtils.afs.authSig(data)
    }
}