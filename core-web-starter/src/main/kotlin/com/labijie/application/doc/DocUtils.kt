package com.labijie.application.doc

import com.fasterxml.jackson.databind.JavaType
import com.labijie.application.IDescribeEnum
import com.labijie.application.JAVA_LONG
import com.labijie.infra.getApplicationName
import com.labijie.infra.utils.ifNullOrBlank
import com.labijie.infra.utils.toLocalDateTime
import io.swagger.v3.core.converter.AnnotatedType
import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.SpecVersion
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.media.IntegerSchema
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.media.StringSchema
import org.apache.commons.text.CaseUtils
import org.springframework.boot.info.GitProperties
import java.lang.reflect.Type
import java.math.BigDecimal
import java.time.format.DateTimeFormatter

/**
 * @author Anders Xiao
 * @date 2023-12-04
 */
object DocUtils {

    fun isEnumType(type: Type): Boolean {
        if (type is Class<*>) {
            return type.isEnum
        }

        val javaType = type is JavaType
        if (javaType) {
            val t = type as JavaType
            return t.isEnumType
        }
        return false
    }

    fun getClassFromType(type: Type): Class<*>? {
        if (type is Class<*>) {
            return type
        }

        val javaType = type is JavaType
        if (javaType) {
            val t = type as JavaType
            return t.rawClass
        }

        return null
    }

    private fun getEnumSchemas(enumValues: Map<Long, String>): List<Schema<*>> {
        return enumValues.map {
            IntegerSchema().apply {
                setConst(it.key)
                description = it.value
                specVersion = SpecVersion.V31
            }
        }
    }

    fun getEnumSchema(baseSchema: Schema<*>, type: Type): Schema<*> {
        if (!isEnumType(type)) {
            return baseSchema
        }
        val dataClass = getClassFromType(type) ?: return@getEnumSchema baseSchema
        val isDescriber = IDescribeEnum::class.java.isAssignableFrom(dataClass)
        val enumValues = dataClass.getEnumConstants()
        val typedValues: Map<Int, String> = enumValues.mapIndexed { index, enumValue ->
            if (isDescriber) {
                (enumValue as IDescribeEnum<*>).code.toInt() to (enumValue).description.ifNullOrBlank { enumValue.toString() }
            } else {
                index.toInt() to enumValue.toString()
            }
        }.toMap()

        val enumDesc = typedValues.map {
            "${it.key}: ${it.value}"
        }.joinToString("\n")

        return IntegerSchema().apply {
            format("enum")
            enum = typedValues.keys.toList()
            setDefault(typedValues.firstNotNullOfOrNull { it.key })
            description = baseSchema.description + "\n" + enumDesc
        }
    }

    private val stringNumberTypes = listOf(
        Long::class.java,
        JAVA_LONG,
        BigDecimal::class.java
    )

    fun isStringNumberType(type: Type): Boolean {
        val dataClass = getClassFromType(type) ?: return false
        return stringNumberTypes.contains(dataClass)
    }

    private fun getNumberFormat(type: Type): String {
        val dataClass = getClassFromType(type)
        if ((dataClass == Long::class.java || dataClass == JAVA_LONG)) {
            return "int64"
        }
        if ((dataClass == BigDecimal::class.java)) {
            return "decimal"
        }
        return "int32"
    }

    fun getNumberSchema(baseSchema: Schema<*>, type: Type): Schema<*> {
        if (!isStringNumberType(type)) {
            return baseSchema
        }
        return StringSchema().apply {
            format(getNumberFormat(type))
            description(baseSchema.description)
        }
    }

    fun createDefaultOpenAPI(applicationName: String, gitProperties: GitProperties? = null, version: String? = null): OpenAPI {
        return OpenAPI()
            .info(
                Info().title("${CaseUtils.toCamelCase(applicationName, true, '-', '@', '_')} API")
                    .apply {
                        if (gitProperties != null) {
                            version(gitProperties.get("build.version") ?: "1.0.0")
                            description(
                                "git commit: ${gitProperties.get("commit.id.abbrev") ?: "--"}   " +
                                        "commit time: ${
                                            gitProperties.commitTime?.toLocalDateTime()
                                                ?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) ?: "--"
                                        }"
                            )
                        }

                        if(!version.isNullOrBlank()) {
                            version(version)
                        }
                    }
            )
    }
}