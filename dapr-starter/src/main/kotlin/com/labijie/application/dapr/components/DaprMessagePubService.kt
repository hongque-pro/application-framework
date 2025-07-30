package com.labijie.application.dapr.components

import com.labijie.application.component.AbstractMessageService
import com.labijie.application.component.IVerificationCodeService
import com.labijie.application.dapr.configuration.DaprProperties
import com.labijie.application.dapr.model.DaprSms
import com.labijie.application.model.VerificationCodeType
import com.labijie.caching.ICacheManager
import io.dapr.client.DaprClient


/**
 * @author Anders Xiao
 * @date 2023-12-04
 */
class DaprMessagePubService(
    private val daprClient: DaprClient,
    private val daprProperties: DaprProperties,
    cacheManager: ICacheManager,
    verificationCodeService: IVerificationCodeService,
) : AbstractMessageService(cacheManager, verificationCodeService) {

    override fun sendCode(dialingCode: Short, phoneNumber: String, captchaType: VerificationCodeType, code: String) {
        daprClient.publishEvent(
            daprProperties.messageService.smsPubsubName,
            daprProperties.messageService.smsTopic,
            DaprSms(dialingCode, phoneNumber, captchaType, code)
            ).block()
    }
}