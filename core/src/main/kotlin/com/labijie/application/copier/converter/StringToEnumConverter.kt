package com.labijie.application.copier.converter

import com.labijie.application.copier.ICopierConverter

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-01
 */
object StringToEnumConverter : ICopierConverter {

    override fun isSupported(source: Class<*>, target: Class<*>): Boolean {
        return (source == String::class.java) && target.isEnum
    }

    private fun getEnumValue(name:String, eunmClass:Class<*>): Any? {
        val enumClz = eunmClass.enumConstants.map {
            it as Enum<*>
        }
        return enumClz.firstOrNull {  it.name.equals(name, true) }
    }

    override fun convert(source: Any?, target: Class<*>): Any? {
        val name = source.toString()
        return getEnumValue(name, target)
    }
}