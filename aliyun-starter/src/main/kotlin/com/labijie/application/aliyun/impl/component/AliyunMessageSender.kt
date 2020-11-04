package com.labijie.application.aliyun.impl.component

import com.labijie.application.aliyun.AliyunUtils
import com.labijie.application.aliyun.impl.AliyunModuleInitializer
import com.labijie.application.component.MessageSenderBase
import com.labijie.caching.ICacheManager
import com.labijie.infra.security.Rfc6238TokenService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-11-18
 */
@Component
@ConditionalOnProperty(name = ["aliyun.sms.enabled"], havingValue = "true", matchIfMissing = true)
class AliyunMessageSender(
    environment: Environment,
    cacheManager: ICacheManager,
    rfc6238TokenService: Rfc6238TokenService,
    private val aliyunUtils: AliyunUtils
)
    : MessageSenderBase(environment, cacheManager, rfc6238TokenService) {

    init {
        AliyunModuleInitializer.messageSenderEnabled = true
    }

    override fun sendSmsGeneralTemplate(phoneNumber: String, template: String, templateParam: Any?) {
        aliyunUtils.sms.sendSms(phoneNumber, template, templateParam)
    }
}