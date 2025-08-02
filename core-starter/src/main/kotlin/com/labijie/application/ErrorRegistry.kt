package com.labijie.application

import com.labijie.application.service.ILocalizationService
import com.labijie.application.service.impl.NoneLocalizationService
import com.labijie.infra.utils.ifNullOrBlank
import com.labijie.infra.utils.logger
import org.apache.commons.lang3.LocaleUtils
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import java.util.Locale
import java.util.SortedMap
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

    fun getErrorCodeMapping(local: Locale?): Map<String, String>
}

internal class ErrorRegistry() : IErrorRegistry, ApplicationContextAware {
    private var localeMessages: MutableMap<Locale, SortedMap<String, String>> = mutableMapOf()
    private var defaultMessages: SortedMap<String, String> = sortedMapOf()

    private var allErrorCode: SortedMap<String, String> = sortedMapOf()
    private val errorCodeMap: MutableMap<Locale,  SortedMap<String, String>> = mutableMapOf()

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
                val errorCode = it.call() as String
                val annotations = it.javaField?.getAnnotationsByType(ErrorDescription::class.java)
                if(!annotations.isNullOrEmpty()) {
                    //Process messages
                    val messageKey = "app.err.${errorCode}"
                    val defaultMessage = errorCode.replace("_", " ")

                    if (defaultMessages.containsKey(messageKey)) {
                        logger.error("Duplex error code '$errorCode' defined. (source code: ${errorObject::class.simpleName}.${it.name})")
                    } else {
                        //添加错误代码映射
                        allErrorCode[errorCode] = defaultMessage

                        //添加国际化消息映射
                        defaultMessages[messageKey] = defaultMessage

                        annotations.forEach {
                            annotation->
                            val local = LocaleUtils.toLocale(annotation.locale)

                            val errorMapping = errorCodeMap.getOrPut(local) { sortedMapOf() }
                            errorMapping.putIfAbsent(errorCode, annotation.description.ifNullOrBlank { defaultMessage })

                            //添加国际化消息映射
                            val messages = localeMessages.getOrPut(local) { sortedMapOf() }
                            messages.putIfAbsent(messageKey, annotation.description.ifNullOrBlank { defaultMessage })
                        }
                    }
                }
            }
        }

    }

    override fun persistMessages() {
        defaultMessages = defaultMessages.toSortedMap()
        allErrorCode = allErrorCode.toSortedMap()

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

        errorCodeMap.forEach { locale ->
            allErrorCode.forEach {
                msg->
                locale.value.putIfAbsent(msg.key, msg.value)
            }
        }
    }

    override fun getErrorMessages(local: Locale?): Map<String, String> {
        val l = local ?: Locale.getDefault()
        return localeMessages[l] ?: defaultMessages
    }

    override fun getErrorCodeMapping(local: Locale?): Map<String, String> {
        val l = local ?: Locale.getDefault()
        return errorCodeMap[l] ?: allErrorCode
    }

    override fun getErrorMessage(code: String, local: Locale?): String? {
        val messageKey = "app.err.${code}"
        return getErrorMessages(local)[messageKey]
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
        val svc = applicationContext.getBeanProvider(ILocalizationService::class.java).ifAvailable
        localizationEnabled = svc != null && (svc !is NoneLocalizationService)
    }
}