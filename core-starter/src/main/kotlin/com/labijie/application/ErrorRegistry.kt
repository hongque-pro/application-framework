package com.labijie.application

import com.labijie.infra.utils.logger
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.javaField

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-14
 */
interface IErrorRegistry {
    fun registerErrors(errorObject: Any)
    val errors: Map<String, String>
}

internal class ErrorRegistry : IErrorRegistry {
    private val list = mutableMapOf<String, String>()

    override fun registerErrors(errorObject: Any) {
        errorObject::class.declaredMemberProperties.forEach {
            if (it.isConst && it.returnType.classifier == String::class) {
                val value = it.call() as String
                val desc = it.javaField?.getAnnotation(ErrorDescription::class.java)?.description ?: value.replace("_", " ")
                if (list.containsKey(value)) {
                    logger.error("Duplex error code '$value' defined. (source code: ${errorObject::class.simpleName}.${it.name})")
                } else {
                    list[value] = desc
                }
            }
        }
    }

    override val errors: Map<String, String>
        get() = list
}