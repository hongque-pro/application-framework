package com.labijie.application.component.impl

import com.labijie.caching.ICacheManager
import com.labijie.application.component.MessageSenderBase
import com.labijie.caching.memory.MemoryCacheManager
import com.labijie.infra.json.JacksonHelper
import com.labijie.infra.security.Rfc6238TokenService
import org.springframework.core.env.Environment

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-09
 */
open class NoneMessageSender(
    environment: Environment? = null,
    cacheManager: ICacheManager? = null,
    rfc6238TokenService: Rfc6238TokenService? = null
) : MessageSenderBase(environment, cacheManager ?: MemoryCacheManager(), rfc6238TokenService ?: Rfc6238TokenService()) {

    constructor():this(null, null, null)

    override val frequencyLimited: Boolean = false

    override fun sendSmsGeneralTemplate(phoneNumber: String, template: String, templateParam: Any?) {
        println("${System.lineSeparator()}>>> Captcha has been sent: ${if (templateParam == null) null else JacksonHelper.serializeAsString(templateParam)}${System.lineSeparator()}" +
                ">>> phone: $phoneNumber")
    }
}