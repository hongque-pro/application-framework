package com.labijie.application

import com.labijie.application.service.LocaleMessage
import com.labijie.infra.utils.logger
import org.springframework.context.MessageSource
import org.springframework.web.server.i18n.LocaleContextResolver
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-14
 */
interface IErrorRegistry {
    fun registerErrors(errorObject: Any, messageSource: MessageSource)
    val errorMessages: Map<String, LocaleMessage>
}

internal class ErrorRegistry : IErrorRegistry {

    private val list = mutableMapOf<String, LocaleMessage>()

    override fun registerErrors(errorObject: Any, messageSource: MessageSource) {
        errorObject::class.declaredMemberProperties.forEach {
            if (it.isConst && it.returnType.classifier == String::class) {
                val value = it.call() as String
                val desc = it.javaField?.getAnnotation(ErrorDescription::class.java)?.description ?: value.replace("_", " ")
                if (list.containsKey(value)) {
                    logger.error("Duplex error code '$value' defined. (source code: ${errorObject::class.simpleName}.${it.name})")
                } else {
                    list[value] = LocaleMessage("err_${value}", desc)
                }
            }
        }
    }

    override val errorMessages: Map<String, LocaleMessage>
        get() = list
}