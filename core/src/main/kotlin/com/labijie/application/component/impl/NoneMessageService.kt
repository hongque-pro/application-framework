package com.labijie.application.component.impl

import com.labijie.caching.ICacheManager
import com.labijie.application.component.AbstractMessageService
import com.labijie.application.model.CaptchaType
import com.labijie.application.model.SendSmsCaptchaParam
import com.labijie.caching.memory.MemoryCacheManager
import com.labijie.infra.json.JacksonHelper
import com.labijie.infra.security.Rfc6238TokenService
import org.springframework.core.env.Environment
import java.lang.StringBuilder

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-09
 */
open class NoneMessageService(
    cacheManager: ICacheManager? = null,
    rfc6238TokenService: Rfc6238TokenService? = null
) : AbstractMessageService(
    cacheManager ?: MemoryCacheManager(),
    rfc6238TokenService ?: Rfc6238TokenService()
) {

    constructor() : this(null, null)

    override val frequencyLimited: Boolean = false

    override fun sendCaptcha(phoneNumber: String, captchaType: CaptchaType, code: String) {
        val msg = StringBuilder().appendLine(" ")
            .appendLine(
                ">>> None Message Sender >>> $phoneNumber >>> $code"
            )
            .appendLine(" ")
            .toString()
        println(msg)
    }

    override fun verifySmsCaptcha(
        phoneNumber: String,
        code: String,
        stamp: String,
        throwIfMissMatched: Boolean
    ): Boolean {
        return true
    }
}