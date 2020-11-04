package com.labijie.application.huawei.component

import com.labijie.application.component.MessageSenderBase
import com.labijie.application.huawei.model.HuaweiMessageTemplates
import com.labijie.application.huawei.model.HuaweiSmsTemplateParam
import com.labijie.application.huawei.utils.HuaweiUtils
import com.labijie.caching.ICacheManager
import com.labijie.infra.security.Rfc6238TokenService
import org.springframework.core.env.Environment
import java.lang.RuntimeException

class HuaweiMessageSender(environment: Environment,
                          cacheManager: ICacheManager,
                          rfc6238TokenService: Rfc6238TokenService,
                          private val huaweiUtils: HuaweiUtils, private val huaweiMessageTemplates: HuaweiMessageTemplates
): MessageSenderBase(environment, cacheManager, rfc6238TokenService) {
    override fun sendSmsGeneralTemplate(phoneNumber: String, template: String, templateParam: Any?) {
        val param = templateParam as Map<String, Any>
        val config = huaweiMessageTemplates.getConfig(template)
        config ?: throw RuntimeException("未配置短信模板，请先配置")
        var huaweiSmsTemplateParam = HuaweiSmsTemplateParam(config.params.map { param[it].toString() }.toTypedArray(), config.sender, config.signature)
        huaweiUtils.sendSmsMessage(phoneNumber, template, huaweiSmsTemplateParam)
    }
}