package com.labijie.application.dapr.components

import com.labijie.application.component.AbstractMessageService
import com.labijie.application.dapr.configuration.DaprProperties
import com.labijie.application.dapr.model.DaprSms
import com.labijie.application.model.SmsCodeType
import com.labijie.caching.ICacheManager
import com.labijie.infra.security.Rfc6238TokenService
import io.dapr.client.DaprClient
import io.dapr.client.DaprClientBuilder


/**
 * @author Anders Xiao
 * @date 2023-12-04
 */
class DaprMessagePubService(
    private val daprClient: DaprClient,
    private val daprProperties: DaprProperties,
    cacheManager: ICacheManager,
    rfc6238TokenService: Rfc6238TokenService
) : AbstractMessageService(cacheManager, rfc6238TokenService) {

    override fun sendCode(phoneNumber: String, captchaType: SmsCodeType, code: String) {
        daprClient.publishEvent(
            daprProperties.messageService.smsPubsubName,
            daprProperties.messageService.smsTopic,
            DaprSms(phoneNumber, captchaType, code)
            ).block()
    }
}