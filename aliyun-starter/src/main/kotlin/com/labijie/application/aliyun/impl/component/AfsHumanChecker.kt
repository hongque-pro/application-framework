package com.labijie.application.aliyun.impl.component

import com.fasterxml.jackson.core.JsonProcessingException
import com.labijie.application.aliyun.AliyunProcessException
import com.labijie.application.aliyun.AliyunUtils
import com.labijie.application.aliyun.impl.AliyunModuleInitializer
import com.labijie.application.aliyun.model.AfsRequest
import com.labijie.application.component.IHumanChecker
import com.labijie.infra.json.JacksonHelper
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import org.springframework.util.Base64Utils

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-11-24
 */
@Component
@ConditionalOnProperty(name = ["aliyun.afs.enabled"], havingValue = "true", matchIfMissing = true)
class AfsHumanChecker(private val aliyunUtils: AliyunUtils) : IHumanChecker {
    init {
        AliyunModuleInitializer.humanCheckerEnabled = true
    }
    override fun check(token:String, clientIp:String): Boolean {
        if (!token.isBlank()) {
            val json = Base64Utils.decodeFromString(token)
            val param = try {
                JacksonHelper.deserialize(json, AfsRequest::class, true)
            } catch (e: JsonProcessingException) {
                null
            }
            if (param != null) {
                param.remoteIp = clientIp
                return try {
                    val r = aliyunUtils.afs.authSig(param)
                    r.success
                }catch (e: AliyunProcessException){
                    true
                }
            }
        }
        return false
    }
}