package com.labijie.application.exposed

import com.labijie.application.*
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.DecimalColumnType
import org.jetbrains.exposed.sql.vendors.currentDialect
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import kotlin.reflect.KClass

/**
 *
 * @Author: Anders Xiao
 * @Date: 2021/12/24
 * @Description:
 */
class DescribeEnumColumnType<E, V>(
    val enumClass: KClass<E>,
    val valueClass: KClass<V>
) : ColumnType() where V : Number, E : IDescribeEnum<V>, E : Enum<E> {

    companion object {
        private val typeMappings = mapOf(
            JAVA_LONG to { currentDialect.dataTypeProvider.longType() },
            JAVA_DOUBLE to { currentDialect.dataTypeProvider.doubleType() },
            JAVA_FLOAT to { currentDialect.dataTypeProvider.floatType() },
            JAVA_INT to { currentDialect.dataTypeProvider.integerType() },
            JAVA_SHORT to { currentDialect.dataTypeProvider.shortType() },
            JAVA_BYTE to { currentDialect.dataTypeProvider.byteType() },
            Int::class.java to { currentDialect.dataTypeProvider.integerType() },
            Short::class.java to { currentDialect.dataTypeProvider.shortType() },
            Byte::class.java to { currentDialect.dataTypeProvider.byteType() },
            Long::class.java to { currentDialect.dataTypeProvider.longType() },
            BigInteger::class.java to { currentDialect.dataTypeProvider.longType() },
            Float::class.java to { currentDialect.dataTypeProvider.floatType() },
            BigDecimal::class.java to { DecimalColumnType(MathContext.DECIMAL64.precision, 20).sqlType() }
        )
    }

    private val sqlTypeValue by lazy {
        typeMappings[valueClass.java]?.invoke()
            ?: error("${valueClass.qualifiedName} is not valid value type for enum ${enumClass.simpleName}")
    }

    override fun sqlType(): String = sqlTypeValue

    override fun valueFromDB(value: Any): E = when (value) {
        is Number -> value.toEnum(enumClass.java)
        else -> error("$value of ${value::class.qualifiedName} is not valid for enum ${enumClass.simpleName}")
    }

    override fun notNullValueToDB(value: Any): Any = when (value) {
        is Number -> value
        is IDescribeEnum<*> -> value.code
        else -> error("$value of ${value::class.qualifiedName} is not valid for enum ${enumClass.simpleName}")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as DescribeEnumColumnType<*, *>

        return enumClass == other.enumClass && valueClass == other.valueClass
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + enumClass.hashCode() + valueClass.hashCode()
        return result
    }
}