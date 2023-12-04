package com.labijie.application.component

import com.labijie.application.configuration.ApplicationCoreProperties
import com.labijie.application.configuration.SmsBaseProperties
import com.labijie.application.crypto.AesException
import com.labijie.application.crypto.DesUtils
import com.labijie.application.exception.InvalidCaptchaException
import com.labijie.application.exception.SmsTooFrequentlyException
import com.labijie.application.model.SmsCodeType
import com.labijie.caching.CacheException
import com.labijie.caching.ICacheManager
import com.labijie.infra.security.Rfc6238TokenService
import com.labijie.infra.utils.ShortId
import com.labijie.infra.utils.logger
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-11
 */
abstract class AbstractMessageService(
    private val cacheManager: ICacheManager,
    private val rfc6238TokenService: Rfc6238TokenService,
) : IMessageService, ApplicationContextAware {

    private var applicationContext: ApplicationContext? = null


    open protected val smsBaseSettings: SmsBaseProperties by lazy {
        this.applicationContext?.getBean(SmsBaseProperties::class.java) ?: throw NoSuchBeanDefinitionException(SmsBaseProperties::class.java)
    }

    open protected val applicationProperties: ApplicationCoreProperties by lazy {
        this.applicationContext?.getBean(ApplicationCoreProperties::class.java) ?: throw NoSuchBeanDefinitionException(ApplicationCoreProperties::class.java)
    }


    protected open val frequencyLimited: Boolean = true

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    private fun sendSmsCodeCore(phoneNumber: String, type: SmsCodeType, securityStamp: String) {
        val key = securityStamp.ifBlank { null }
        val code = rfc6238TokenService.generateCodeString(key, phoneNumber, smsBaseSettings.messageExpire)
        sendCode(phoneNumber, type, code)
    }

    protected open fun <T> limitFrequency(captchaType: SmsCodeType, phoneNumber: String, sendAction: () -> T): T {

        val key = "sms:${captchaType}:send:$phoneNumber"
        try {
            val lastSend = (cacheManager.get(key, Long::class.java) as? Long) ?: 0
            if (this.frequencyLimited && (System.currentTimeMillis() - lastSend) < smsBaseSettings.sendRateLimit.toMillis()) {
                throw SmsTooFrequentlyException()
            }
        } catch (e: CacheException) {
            logger.error("Get sms send frequency from cache fault. key: $key", e)
        }
        val r = sendAction.invoke()
        try {
            cacheManager.set(key, System.currentTimeMillis(), smsBaseSettings.sendRateLimit.toMillis())
        } catch (e: CacheException) {
            logger.error("Set sms send frequency to cache fault. key: $key", e)
        }
        return r
    }


    protected abstract fun sendCode(phoneNumber: String, captchaType: SmsCodeType, code: String)

    override fun verifySmsCode(code: String, token: String, throwIfMissMatched: Boolean): Boolean {
        if(code.isBlank() || token.isBlank()) {
            return false
        }

        try {
            val str = DesUtils.decrypt(token, applicationProperties.desSecret)
            val segments = str.split(":")
            if(segments.size != 2){
                return false
            }
            val clientStamp = segments[0]
            val phoneNumber = segments[1]
            return verify(phoneNumber, code, clientStamp, throwIfMissMatched)

        }catch (e:AesException){
            return false
        }
    }

    final override fun sendSmsCode(phoneNumber: String, type: SmsCodeType): SmsToken {
        val clientStamp = ShortId.newId().replace(":", "_")
        val token = SmsToken(token = DesUtils.encrypt("${clientStamp}:${phoneNumber}", applicationProperties.desSecret))
        if (frequencyLimited) {
            limitFrequency(type, phoneNumber) {
                sendSmsCodeCore(phoneNumber, type, clientStamp)
            }
        } else {
            sendSmsCodeCore(phoneNumber, type, clientStamp)
        }
        return token
    }


    private fun verify(
        phoneNumber: String,
        code: String,
        clientStamp: String,
        throwIfMissMatched: Boolean
    ): Boolean {
        val result = this.rfc6238TokenService.validateCodeString(code, clientStamp, phoneNumber, smsBaseSettings.messageExpire)
        if (throwIfMissMatched && !result) {
            throw InvalidCaptchaException()
        }
        return result
    }
}