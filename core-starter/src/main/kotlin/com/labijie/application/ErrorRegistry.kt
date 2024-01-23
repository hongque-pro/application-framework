package com.labijie.application

import com.labijie.application.service.ILocalizationService
import com.labijie.application.service.impl.NoneLocalizationService
import com.labijie.infra.utils.ifNullOrBlank
import com.labijie.infra.utils.logger
import org.apache.commons.lang3.LocaleUtils
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-14
 */
interface IErrorRegistry {
    fun registerErrors(errorObject: Any, localizationService: ILocalizationService)
    val errorMessageCodes: Map<String, String>
    val defaultMessages: Map<String, String>
    fun isLocalizationEnabled(): Boolean
}

internal class ErrorRegistry() : IErrorRegistry, ApplicationContextAware {
    override val errorMessageCodes:MutableMap<String, String> = mutableMapOf()
    override val defaultMessages: MutableMap<String, String> = mutableMapOf()

    private var localizationEnabled = false;

    override fun isLocalizationEnabled(): Boolean {
        return localizationEnabled
    }

    override fun registerErrors(errorObject: Any, localizationService: ILocalizationService) {
        errorObject::class.declaredMemberProperties.forEach {
            if (it.isConst && it.returnType.classifier == String::class) {
                val value = it.call() as String
                val annotation = it.javaField?.getAnnotation(ErrorDescription::class.java)
                if(annotation != null) {
                    val code = "app.err.${value}"
                    if (errorMessageCodes.containsKey(value)) {
                        logger.error("Duplex error code '$value' defined. (source code: ${errorObject::class.simpleName}.${it.name})")
                    } else {
                        errorMessageCodes[value] = code
                        if(localizationService !is NoneLocalizationService) {
                            val local = LocaleUtils.toLocale(annotation.locale)
                            localizationService.setMessage(
                                code,
                                annotation.description.ifNullOrBlank { value.replace("_", " ") },
                                local
                            )
                        } else {
                            defaultMessages[value] = annotation.description
                        }
                    }
                }
            }
        }
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        val svc = applicationContext.getBeanProvider(ILocalizationService::class.java).ifAvailable
        localizationEnabled = svc != null && (svc !is NoneLocalizationService)
    }
}