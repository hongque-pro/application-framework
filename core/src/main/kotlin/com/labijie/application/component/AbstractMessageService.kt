package com.labijie.application.component

import com.labijie.application.configuration.SmsBaseSettings
import com.labijie.application.exception.InvalidCaptchaException
import com.labijie.application.exception.SmsTooFrequentlyException
import com.labijie.application.model.CaptchaType
import com.labijie.application.model.SendSmsCaptchaParam
import com.labijie.caching.CacheException
import com.labijie.caching.ICacheManager
import com.labijie.infra.security.Rfc6238TokenService
import com.labijie.infra.utils.logger
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-11
 */
abstract class AbstractMessageService(
    private val cacheManager: ICacheManager,
    private val rfc6238TokenService: Rfc6238TokenService
) : IMessageService, ApplicationContextAware {

    private var applicationContext:ApplicationContext? = null


    protected val smsBaseSettings: SmsBaseSettings by lazy {
        this.applicationContext?.getBean(SmsBaseSettings::class.java) ?: throw RuntimeException()
    }


    protected open val frequencyLimited:Boolean = true

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }


    final override fun sendSmsCaptcha(param: SendSmsCaptchaParam) {
        if(frequencyLimited) {
            limitFrequency(param.captchaType, param.phoneNumber) {
                sendSmsCaptchaCore(param)
            }
        }else{
            sendSmsCaptchaCore(param)
        }
    }


    private fun sendSmsCaptchaCore(param: SendSmsCaptchaParam) {
        val stamp = param.clientStamp
        val key = stamp.ifBlank { null }
        val code = rfc6238TokenService.generateCodeString(key, param.phoneNumber)
        sendCaptcha(param.phoneNumber, param.captchaType, code)
    }

    protected open fun <T> limitFrequency(captchaType: CaptchaType, phoneNumber: String, sendAction: () -> T): T {

        val key = "sms:${captchaType}:send:$phoneNumber"
        try {
          val lastSend = (cacheManager.get(key, Long::class.java) as? Long) ?: 0
          if (this.frequencyLimited && (System.currentTimeMillis() - lastSend) < smsBaseSettings.sendRateLimit.toMillis()) {
                throw SmsTooFrequentlyException()
            }
        }catch (e:CacheException){
            logger.error("Get sms send frequency from cache fault. key: $key", e)
        }
        val r = sendAction.invoke()
        try {
            cacheManager.set(key, System.currentTimeMillis(), smsBaseSettings.sendRateLimit.toMillis())
        } catch (e:CacheException){
            logger.error("Set sms send frequency to cache fault. key: $key", e)
        }
        return r
    }

    protected abstract fun sendCaptcha(phoneNumber: String, captchaType: CaptchaType, code: String)

    override fun verifySmsCaptcha(
        phoneNumber: String,
        code: String,
        stamp: String,
        throwIfMissMatched: Boolean
    ): Boolean {
        val result = this.rfc6238TokenService.validateCodeString(code, stamp, phoneNumber)
        if (throwIfMissMatched && !result) {
            throw InvalidCaptchaException()
        }
        return result
    }
}