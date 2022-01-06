package com.labijie.application.component

import com.labijie.application.async.handler.MessageHandler
import com.labijie.application.configuration.SmsBaseSettings
import com.labijie.application.configuration.SmsTemplates
import com.labijie.application.exception.InvalidCaptchaException
import com.labijie.application.exception.SmsTooFrequentlyException
import com.labijie.application.model.CaptchaType
import com.labijie.application.model.SendSmsCaptchaParam
import com.labijie.application.model.SendSmsTemplateParam
import com.labijie.application.toMap
import com.labijie.caching.CacheException
import com.labijie.caching.ICacheManager
import com.labijie.infra.json.JacksonHelper
import com.labijie.infra.security.Rfc6238TokenService
import com.labijie.infra.spring.configuration.isDevelopment
import com.labijie.infra.utils.logger
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.core.env.Environment
import org.springframework.messaging.support.GenericMessage

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-11
 */
abstract class MessageSenderBase constructor(
    environment: Environment?,
    private val cacheManager: ICacheManager,
    protected val rfc6238TokenService: Rfc6238TokenService
) : IMessageSender, ApplicationContextAware {

    private var smsSource: StreamBridge? = null
    private var smsSourceLoaded = false
    private var applicationContext:ApplicationContext? = null

    protected val smsTemplates: SmsTemplates by lazy {
        this.applicationContext?.getBean(SmsTemplates::class.java) ?: throw RuntimeException()
    }

    protected val smsBaseSettings: SmsBaseSettings by lazy {
        this.applicationContext?.getBean(SmsBaseSettings::class.java) ?: throw RuntimeException()
    }

    protected  val asyncSource: StreamBridge?
    get() {
        val context = applicationContext
        if(!smsSourceLoaded && context != null) {
            smsSource = try {
                context.getBean(StreamBridge::class.java)
            } catch (e: NoSuchBeanDefinitionException) {
                null
            }
            finally {
                smsSourceLoaded = true
            }
        }
        return smsSource
    }


    protected val isDevelopment = environment?.isDevelopment ?: false
    protected open val frequencyLimited:Boolean = true

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    protected open fun sendLoginCaptcha(phoneNumber: String, code: String, async: Boolean) {
        this.sendSmsCore(SendSmsTemplateParam(
            phoneNumber = phoneNumber,
            template = this.smsTemplates.captchaLogin,
            templateParam = SendCaptchaParam(code).toMap()
        ), async)
    }

    protected open fun sendSmsRegisterCaptcha(phoneNumber: String, code: String, async: Boolean) {
        this.sendSmsCore(
            SendSmsTemplateParam(
            phoneNumber = phoneNumber,
            template = this.smsTemplates.captchaRegister,
            templateParam = SendCaptchaParam(code).toMap()
        ), async)
    }

    protected open fun sendChangePhoneCaptcha(phoneNumber: String, code: String, async: Boolean) {
        this.sendSmsCore(SendSmsTemplateParam(
            phoneNumber = phoneNumber,
            template = this.smsTemplates.captchaChangePhone,
            templateParam = SendCaptchaParam(code).toMap()
        ), async)
    }

    protected open fun sendForgotPasswordCaptcha(phoneNumber: String, code: String, async: Boolean) {
        this.sendSmsCore(SendSmsTemplateParam(
            phoneNumber = phoneNumber,
            template = this.smsTemplates.captchaForgotPassword,
            templateParam = SendCaptchaParam(code).toMap()
        ), async)
    }

    protected open fun sendSmsGeneralCaptcha(phoneNumber: String, code: String, async: Boolean) {
        this.sendSmsCore(SendSmsTemplateParam(
            phoneNumber = phoneNumber,
            template = this.smsTemplates.captchaGeneral,
            templateParam = SendCaptchaParam(code).toMap()
        ), async)
    }

    override fun sendSmsCaptcha(param: SendSmsCaptchaParam, async: Boolean) {
        this.sendSmsCaptchaCore(param, async)
    }

    override fun sendSmsTemplate(param: SendSmsTemplateParam, async: Boolean, checkTimeout: Boolean) {
        if (checkTimeout && (System.currentTimeMillis() - param.sendTime) >=
            this.smsBaseSettings.async.messageExpiration.toMillis()) {
            logger.warn("sms message expired: ${JacksonHelper.serializeAsString(param)}")
            return
        }
        limitFrequency(param.template, param.phoneNumber) {
            sendSmsCore(param, async)
        }
    }

    private fun sendSmsCore(param: SendSmsTemplateParam, async: Boolean) {
        if (async && asyncSource != null) {
            asyncSource?.send(MessageHandler.STREAM_SMS_OUT, GenericMessage(param))
        } else {
            this.sendSmsGeneralTemplate(param.phoneNumber, param.template, param.templateParam)
        }
    }

    private fun sendSmsCaptchaCore(param: SendSmsCaptchaParam, async: Boolean) {
        val code = rfc6238TokenService.generateCodeString(param.stamp, param.modifier)
        when (param.captchaType) {
            CaptchaType.Register ->
                this.sendSmsRegisterCaptcha(param.phoneNumber, code, async)
            CaptchaType.ChangePhone ->
                this.sendChangePhoneCaptcha(param.phoneNumber, code, async)
            CaptchaType.ForgotPassword ->
                this.sendForgotPasswordCaptcha(param.phoneNumber, code, async)
            CaptchaType.Login ->
                this.sendLoginCaptcha(param.phoneNumber, code, async)
            CaptchaType.General ->
                this.sendSmsGeneralCaptcha(param.phoneNumber, code, async)
        }
    }

    protected open fun <T> limitFrequency(captchaType: String, phoneNumber: String, sendAction: () -> T): T {

        val key = "sms:$captchaType:send:$phoneNumber"
        try {
          val lastSend = if(this.frequencyLimited) (cacheManager.get(key, Long::class.java) as? Long) ?: 0 else 0
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

    protected abstract fun sendSmsGeneralTemplate(phoneNumber: String, template: String, templateParam: Any?)

    override fun verifySmsCaptcha(code: String, stamp: String, modifier: String?, throwIfMissMatched: Boolean): Boolean {
        val result = this.rfc6238TokenService.validateCodeString(code, stamp, modifier)
        if (throwIfMissMatched && !result) {
            throw InvalidCaptchaException()
        }
        return result
    }

    private data class SendCaptchaParam(var code:String = "")
}