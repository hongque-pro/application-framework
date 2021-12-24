package com.labijie.application.exposed

import com.labijie.application.IDescribeEnum
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import kotlin.reflect.KClass

/**
 *
 * @Author: Anders Xiao
 * @Date: 2021/12/24
 * @Description:
 */
object ExposedUtils {
    fun <E, V> Table.describeEnum(name: String, enumType: KClass<E>, valueType: KClass<V>): Column<E>
            where V : Number, E : IDescribeEnum<V>, E : Enum<E> {
        return this.registerColumn<E>(name, DescribeEnumColumnType(enumType, valueType))
    }
}