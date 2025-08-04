/**
 * @author Anders Xiao
 * @date 2024-06-08
 */
package com.labijie.application

import org.springframework.beans.BeansException
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import java.lang.reflect.Constructor
import java.lang.reflect.Parameter
import java.lang.reflect.ParameterizedType
import java.time.Duration
import kotlin.reflect.KClass


object ObjectUtils {
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> constructObjectWithInjection(clazz: KClass<T>, context: ApplicationContext?): T {
        return constructInjection(clazz, context) as T
    }

    private fun constructInjection(clazz: KClass<*>, context: ApplicationContext?): Any {
        val moduleClass = clazz.java
        val ctor = getConstructor(moduleClass)
        if(ctor.parameters.isEmpty()){
            return ctor.newInstance()
        }
        val ctx = context ?: SpringContext.current
        return try {
            val parameters = ctor.parameters.map { param ->
                getBean(param, ctx)
            }
            ctor.newInstance(*parameters.toTypedArray())
        } catch (e: BeansException) {
            val error =
                "Construct object from bean fault ( object class:  ${moduleClass.name})"
            throw RuntimeException(error, e)
        }
    }

    private fun getBean(param: Parameter, ctx: ApplicationContext): Any? {
        val isObjectProvider =
            param.type == ObjectProvider::class.java && param.parameterizedType != null && param.parameterizedType is ParameterizedType
        return if (isObjectProvider) {
            val type = (param.parameterizedType as ParameterizedType).actualTypeArguments[0]
            ctx.getBeanProvider(type as Class<*>)
        } else {
            ctx.getBean(param.type)
        }
    }


    private fun getConstructor(moduleClass: Class<*>): Constructor<*> {
        val constructors = moduleClass.constructors
        val ctor = constructors.firstOrNull {
            it.declaredAnnotations.any { anno -> anno.annotationClass.java == Autowired::class.java }
        } ?: constructors.maxBy { it.parameters.size }
        return ctor ?: throw RuntimeException("Unable to got declared constructor from type: ${moduleClass.simpleName}")
    }

    fun Duration.toShortString(): String {
        val millis: Long = this.toMillis()

        if (millis < 1000) {
            return millis.toString() + "ms"
        } else if (millis < 60000) {
            val seconds = millis / 1000
            return seconds.toString() + "s"
        } else if (millis < 3600000) {
            val minutes = millis / 60000.0
            return String.format("%.1fm", minutes)
        } else {
            val hours = millis / 3600000.0
            return String.format("%.1fh", hours)
        }
    }
}