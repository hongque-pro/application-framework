/**
 *
 * @Author: Anders Xiao
 * @Date: 2021/12/24
 * @Description:
 */
package com.labijie.application.exposed

import com.labijie.application.IDescribeEnum
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

object ExposedUtils {

    fun <TEnum, TCode> Table.describeEnum(name: String, enumType: KClass<TEnum>, valueType: KClass<TCode>): Column<TEnum>
            where TCode : Number, TEnum : IDescribeEnum<TCode>, TEnum : Enum<TEnum> {
        return this.registerColumn(name, DescribeEnumColumnType(enumType, valueType))
    }

    inline fun <reified TEnum, reified TCode> Table.describeEnum(name: String): Column<TEnum>
            where TCode : Number, TEnum : IDescribeEnum<TCode>, TEnum : Enum<TEnum> {
        val enumClass = TEnum::class
        val codeClass = TCode::class
        return this.registerColumn(name, DescribeEnumColumnType(enumClass, codeClass))
    }
}