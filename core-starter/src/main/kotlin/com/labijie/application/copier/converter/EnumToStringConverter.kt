package com.labijie.application.copier.converter

import com.labijie.application.copier.ICopierConverter
import org.springframework.cglib.core.Converter

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-01
 */
object EnumToStringConverter : ICopierConverter {
    override fun isSupported(source: Class<*>, target: Class<*>): Boolean {
        return (source.isEnum) && target == String::class.java
    }

    override fun convert(source: Any?, target: Class<*>): Any? {
        return source.toString()
    }
}