package com.labijie.application.copier.converter

import com.labijie.application.*
import com.labijie.application.copier.ICopierConverter
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-10-01
 *
 * Int::class.java ||
 * Short::class.java ||
 * Byte::class.java ||
 * Long::class.java ||
 * BigInteger::class.java ||
 * BigInteger::class.java ||
 * Float::class.java ||
 * BigDecimal::class.java
 */
object EnumToNumberConverter : ICopierConverter {

    override fun isSupported(source: Class<*>, target: Class<*>): Boolean {
        return (source.isEnum) && target.isNumeric
    }

    override fun convert(source: Any?, target: Class<*>): Any? {
        val desc = source as? IDescribeEnum<*>
        val value = desc?.code ?: (source as Enum<*>).ordinal
        return when(target){
            Short::class.java, JAVA_SHORT->value.toShort()
            Byte::class.java, JAVA_BYTE->value.toByte()
            Long::class.java, JAVA_LONG->value.toLong()
            BigInteger::class.java->value.toInt().toBigInteger()
            Float::class.java, JAVA_FLOAT->value.toFloat()
            BigDecimal::class.java->BigDecimal(value.toString())
            Int::class.java, JAVA_INT->value
            else -> value

        }
    }
}