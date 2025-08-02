package com.labijie.application.email.service.impl

import com.labijie.application.component.AbstractRateLimitService
import com.labijie.application.service.IOneTimeCodeService
import com.labijie.application.email.configuration.EmailServiceProperties
import com.labijie.application.email.provider.DummyEmailProvider
import com.labijie.application.email.provider.IEmailServiceProvider
import com.labijie.application.email.service.IEmailService
import com.labijie.application.email.model.TemplatedMail
import com.labijie.application.model.VerificationCodeType
import com.labijie.application.model.OneTimeGenerationResult
import com.labijie.caching.ICacheManager
import com.labijie.infra.utils.ShortId
import org.slf4j.LoggerFactory
import java.time.Duration

/**
 * @author Anders Xiao
 * @date 2025/7/29
 */
class DefaultEmailService(
    private val properties: EmailServiceProperties,
    cacheManager: ICacheManager,
    private val oneTimeCodeService: IOneTimeCodeService,
    providers: Collection<IEmailServiceProvider>
) : AbstractRateLimitService(cacheManager), IEmailService {

    companion object {
        private val logger by lazy {
            LoggerFactory.getLogger(DefaultEmailService::class.java)
        }
    }

    private val providers = mutableMapOf<String, IEmailServiceProvider>()


    private val mainProvider by lazy {

        val main = properties.mainProvider
        if(main.isBlank()) {
            return@lazy this.providers.firstNotNullOfOrNull { it.value } ?: DummyEmailProvider
        }

        val p = this.providers[main.lowercase()]

        if(p == null) {
            val used = this.providers.firstNotNullOfOrNull { it.value } ?: DummyEmailProvider
            logger.warn("Email service main provider '${properties.mainProvider}' not found, provider '${used.name}' used as main provider.")
            used
        }else {
            p
        }
    }

    init {
        providers.forEach {
           if(this.providers.putIfAbsent(it.name.lowercase(), it) == null) {
               logger.info("Email provider ${it.name} registered.")
           }
            this.providers.putIfAbsent("dummy", DummyEmailProvider)
        }
    }

    override fun sendTemplated(mail: TemplatedMail) {
        val id = "mail:${mail.to}:t_${mail.templateId}"
        rateLimit(id, "Send email") {
            this.mainProvider.sendTemplateMailAsync(mail)
        }
    }

    override fun getRateLimitation(): Duration {
        return properties.sendRateLimit
    }


    override fun sendVerificationCode(to: String, type: VerificationCodeType): OneTimeGenerationResult {

        val code =  oneTimeCodeService.generateMailCode(to)

        val id = "mail:${to}:${type.toString().lowercase()}_code"
        rateLimit(id, "Send email verification code") {
            this.mainProvider.sendVerificationCodeAsync(to, code.code, type)
        }
        return OneTimeGenerationResult(code.stamp)
    }

}