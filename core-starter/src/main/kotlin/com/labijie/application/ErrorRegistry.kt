package com.labijie.application

import com.labijie.application.service.ILocalizationService
import com.labijie.application.service.impl.NoneLocalizationService
import com.labijie.infra.utils.ifNullOrBlank
import com.labijie.infra.utils.logger
import org.apache.commons.lang3.LocaleUtils
import org.hibernate.validator.internal.engine.messageinterpolation.DefaultLocaleResolver
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.i18n.LocaleContextHolder
import java.util.Locale
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-14
 */
interface IErrorRegistry {
    fun registerErrors(errorObject: Any, localizationService: ILocalizationService)
    fun isLocalizationEnabled(): Boolean
    fun persistMessages()

    fun getErrorMessage(code: String, local: Locale?): String?
    fun getErrorMessages(local: Locale?): Map<String, String>
}

internal class ErrorRegistry() : IErrorRegistry, ApplicationContextAware {
    private val localeMessages: MutableMap<Locale, MutableMap<String, String>> = mutableMapOf()
    private val defaultMessages: MutableMap<String, String> = mutableMapOf()

    private var localizationEnabled = false

    override fun isLocalizationEnabled(): Boolean {
        return localizationEnabled
    }

    private var applicationContext: ApplicationContext? = null

    private val localizationService by lazy {
        applicationContext?.getBean(ILocalizationService::class.java) ?: NoneLocalizationService()
    }

    override fun registerErrors(errorObject: Any, localizationService: ILocalizationService) {

        errorObject::class.declaredMemberProperties.forEach {
            if (it.isConst && it.returnType.classifier == String::class) {
                val value = it.call() as String
                val annotations = it.javaField?.getAnnotationsByType(ErrorDescription::class.java)
                if(!annotations.isNullOrEmpty()) {
                    val code = "app.err.${value}"
                    if (defaultMessages.containsKey(value)) {
                        logger.error("Duplex error code '$value' defined. (source code: ${errorObject::class.simpleName}.${it.name})")
                    } else {
                        val defaultMessage = value.replace("_", " ")
                        defaultMessages[value] = defaultMessage

                        annotations.forEach {
                            annotation->
                            val local = LocaleUtils.toLocale(annotation.locale)
                            val records = localeMessages.getOrPut(local) { mutableMapOf() }
                            records.putIfAbsent(code, annotation.description.ifNullOrBlank { defaultMessage })
                        }
                    }
                }
            }
        }

    }

    override fun persistMessages() {
        if(localizationService !is NoneLocalizationService) {
            localeMessages.forEach { local ->
                localizationService.setMessages(local.value, local.key)
            }
        }
        //对齐所有的 errorCode
        localeMessages.forEach { locale ->
            defaultMessages.forEach {
                msg->
                locale.value.putIfAbsent(msg.key, msg.value)
            }
        }
    }

    override fun getErrorMessages(local: Locale?): Map<String, String> {
        val l = local ?: Locale.getDefault()
        return localeMessages[l] ?: defaultMessages
    }

    override fun getErrorMessage(code: String, local: Locale?): String? {
        return getErrorMessages(local)[code]
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
        val svc = applicationContext.getBeanProvider(ILocalizationService::class.java).ifAvailable
        localizationEnabled = svc != null && (svc !is NoneLocalizationService)
    }
}