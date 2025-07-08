package com.labijie.application.copier

import com.labijie.application.copier.converter.*
import org.springframework.cglib.beans.BeanCopier
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-01
 */
object BeanCopierUtils {
    private val copiers = ConcurrentHashMap<String, BeanCopier>()
    private val converters = ConcurrentHashMap<Class<*>, ICopierConverter>()

    init {
        this.registerConverter(EnumToNumberConverter)
        this.registerConverter(EnumToStringConverter)
        this.registerConverter(NumberToEnumConverter)
        this.registerConverter(StringToEnumConverter)
        this.registerConverter(WrapTypeToPrimitiveType)
    }

    private val converter : CompositeConverter by lazy {
        CompositeConverter(this.converters.values.toTypedArray())
    }

    fun registerConverter(converter: ICopierConverter){
        converters[converter::class.java] = converter;
    }

    fun  copyProperties(source: Any, target: Any){
        val key = "${source::class.java.name}>${target::class.java.name}"
        val copier = copiers.getOrPut(key){
            BeanCopier.create(source::class.java, target::class.java, true)
        }
        copier.copy(source, target, converter)
    }

    class CompositeConverter(private val innerConverters:Array<ICopierConverter>) : org.springframework.cglib.core.Converter {
        override fun convert(source: Any?, target: Class<*>, context: Any?): Any? {
            if(source == null){
                return null
            }
            val sourceType = source::class.java
            innerConverters.forEach {
                 val supported = it.isSupported(sourceType, target)
                 if(supported){
                     return it.convert(source, target)
                 }
            }
            return source
        }
    }
}