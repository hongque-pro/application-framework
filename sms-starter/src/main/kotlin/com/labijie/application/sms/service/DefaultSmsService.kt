package com.labijie.application.sms.service

import com.labijie.application.component.AbstractRateLimitService
import com.labijie.application.model.OneTimeGenerationResult
import com.labijie.application.model.VerificationCodeType
import com.labijie.application.service.IOneTimeCodeService
import com.labijie.application.sms.configuration.SmsServiceProperties
import com.labijie.application.sms.model.TemplatedMessage
import com.labijie.application.sms.provider.DummySmsServiceProvider
import com.labijie.application.sms.provider.ISmsServiceProvider
import com.labijie.caching.ICacheManager
import org.slf4j.LoggerFactory
import java.time.Duration

/**
 * @author Anders Xiao
 * @date 2025/7/30
 */
class DefaultSmsService(
    private val properties: SmsServiceProperties,
    cacheManager: ICacheManager,
    providers: Collection<ISmsServiceProvider>,
    private val oneTimeCodeService: IOneTimeCodeService,
) : AbstractRateLimitService(cacheManager), ISmsService {

    companion object {
        private val logger by lazy {
            LoggerFactory.getLogger(DefaultSmsService::class.java)
        }
    }

    override val rateLimited: Boolean
        get() = true

    override fun getRateLimitation(): Duration {
        return properties.sendRateLimit
    }

    private val providers = mutableMapOf<String, ISmsServiceProvider>()


    private val mainProvider by lazy {

        val main = properties.mainProvider
        val mainProvider =  if(main.isBlank()) {
            this.providers.firstNotNullOfOrNull { it.value } ?: DummySmsServiceProvider
        }
        else {
            val p = this.providers[main.lowercase()]

            if (p == null) {
                val used = this.providers.firstNotNullOfOrNull { it.value } ?: DummySmsServiceProvider
                logger.warn("SMS service main provider '${properties.mainProvider}' not found.")
                used
            } else {
                p
            }
        }
        logger.info("SMS service use '${mainProvider.name}' as main provider.")
        mainProvider
    }

    init {
        providers.forEach {
            if (this.providers.putIfAbsent(it.name.lowercase(), it) == null) {
                logger.info("Sms provider ${it.name} registered.")
            }
            this.providers.putIfAbsent("dummy", DummySmsServiceProvider)
        }
    }


    override fun sendTemplated(message: TemplatedMessage) {

        if (rateLimited) {
            val id = "sms:${message.dialingCode}${message.phoneNumber}:t_${message.templateId}"
            rateLimit(id, "Send sms verification code") {
                mainProvider.sendTemplatedAsync(message)
            }
        } else {
            mainProvider.sendTemplatedAsync(message)
        }

    }


    override fun sendVerificationCode(
        dialingCode: Short,
        phoneNumber: String,
        type: VerificationCodeType
    ): OneTimeGenerationResult {
        val code = oneTimeCodeService.generatePhoneCode(dialingCode, phoneNumber)

        val id = "sms:${dialingCode}${phoneNumber}:${type.toString().lowercase()}_code"
        if (rateLimited) {
            rateLimit(id, "Send sms verification code") {
                mainProvider.sendVerificationCodeAsync(dialingCode, phoneNumber, code.code, code.expiration, type)
            }
        } else {
            mainProvider.sendVerificationCodeAsync(dialingCode, phoneNumber, code.code, code.expiration, type)
        }

        val verificationToken = OneTimeGenerationResult(code.stamp)
        return verificationToken
    }


}