package com.labijie.application.doc

import com.fasterxml.jackson.databind.JavaType
import com.labijie.application.JAVA_DOUBLE
import com.labijie.application.JAVA_LONG
import io.swagger.v3.core.converter.AnnotatedType
import io.swagger.v3.core.util.Json
import io.swagger.v3.oas.models.media.IntegerSchema
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.media.StringSchema
import org.springdoc.core.customizers.PropertyCustomizer
import org.springframework.stereotype.Component
import java.math.BigDecimal


/**
 * @author Anders Xiao
 * @date 2023-12-01
 */

@Component
class DocPropertyCustomizer : PropertyCustomizer {
    private fun isEnumType(type: AnnotatedType): Boolean {
        val javaType = type.type is JavaType
        if (javaType) {
            val t = type.type as JavaType
            return t.isEnumType
        }
        return false
    }

    private fun isStringNumberType(type: AnnotatedType): Boolean {
        val javaType = type.type is JavaType
        if (javaType) {
            val t = type.type as JavaType
            return (t.rawClass == Long::class.java || t.rawClass == JAVA_LONG || t.rawClass == BigDecimal::class.java)
        }
        return false
    }

    private fun getNumberFormat(type: AnnotatedType): String {
        val javaType = type.type is JavaType
        if (javaType) {
            val t = type.type as JavaType
            if((t.rawClass == Long::class.java || t.rawClass == JAVA_LONG)){
                return "int64"
            }
            if((t.rawClass == BigDecimal::class.java)) {
                return "decimal"
            }
        }
        return "int32"
    }

    override fun customize(property: Schema<*>, type: AnnotatedType): Schema<*> {
        if (property is StringSchema && isEnumType(type)) {
            val enumValues = (type.type as JavaType).rawClass.getEnumConstants()

            return IntegerSchema().apply {
                format("enum")
                description = property.description + "\n" +
                        enumValues.mapIndexed { index, any -> "$index: $any" }.joinToString("\n")
            }
        }
        if (property is IntegerSchema && isStringNumberType(type)) {

            return StringSchema().apply {
                format(getNumberFormat(type))
                description(property.description)
            }
        }
        return property
    }
}