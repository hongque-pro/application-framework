package com.labijie.application.copier.converter

import com.labijie.application.*
import com.labijie.application.copier.ICopierConverter
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-01
 */
object NumberToEnumConverter  : ICopierConverter {

    override fun isSupported(source: Class<*>, target: Class<*>): Boolean {
        return (source.isNumeric) && target.isEnum
    }

    private fun getEnumValue(value: Int, eunmClass:Class<*>): Any? {
        return getEnumFromNumber(eunmClass, value)
    }

    override fun convert(source: Any?, target: Class<*>): Any? {
        return if(source != null) {
            val value = when (source::class.java) {
                JAVA_SHORT, Short::class.java -> (source as Short).toInt()
                JAVA_BYTE, Byte::class.java -> (source as Byte).toInt()
                JAVA_LONG, Long::class.java -> (source as Long).toInt()
                BigInteger::class.java -> (source as BigInteger).toInt()
                JAVA_FLOAT, Float::class.java -> (source as Float).toInt()
                BigDecimal::class.java -> (source as BigDecimal).toInt()
                JAVA_INT, Int::class.java -> (source as Int)
                else -> (source as Int)
            }
            getEnumValue(value, target)
        }else{
            null
        }
    }
}