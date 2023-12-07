package com.labijie.application.doc

import io.swagger.v3.oas.models.parameters.Parameter
import org.springdoc.core.customizers.ParameterCustomizer
import org.springframework.core.KotlinDetector
import org.springframework.core.MethodParameter
import org.springframework.core.Ordered
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ValueConstants
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.kotlinFunction

/**
 * @author Anders Xiao
 * @date 2023-12-04
 */
//TODO: use number for parameter, but spring doc is not support enum well
@Suppress("unused")
class DocParameterCustomizer : ParameterCustomizer, Ordered {
    override fun customize(parameterModel: Parameter?, methodParameter: MethodParameter): Parameter? {

        if (parameterModel == null) return null
        if (KotlinDetector.isKotlinReflectPresent() && KotlinDetector.isKotlinType(methodParameter.parameterType)) {
            val kParameter = methodParameter.toKParameter()
            if (kParameter != null) {
                val parameterDoc = AnnotatedElementUtils.findMergedAnnotation(
                    AnnotatedElementUtils.forAnnotations(*methodParameter.parameterAnnotations),
                    io.swagger.v3.oas.annotations.Parameter::class.java
                )
                val requestParam = AnnotatedElementUtils.findMergedAnnotation(
                    AnnotatedElementUtils.forAnnotations(*methodParameter.parameterAnnotations),
                    RequestParam::class.java
                )
                // Swagger @Parameter annotation takes precedence
                if (parameterDoc != null && parameterDoc.required)
                    parameterModel.required = parameterDoc.required
                // parameter is not required if a default value is provided in @RequestParam
                else if (requestParam != null && requestParam.defaultValue != ValueConstants.DEFAULT_NONE)
                    parameterModel.required = false
                else
                    parameterModel.required =
                        kParameter.type.isMarkedNullable == false
            }
        }

        val classType = methodParameter.parameterType
        if (DocUtils.isEnumType(classType)) {
            val schema = DocUtils.getEnumSchema(parameterModel.schema, classType)
            parameterModel.schema = schema
        }
        if (DocUtils.isStringNumberType(classType)) {
            val schema = DocUtils.getNumberSchema(parameterModel.schema, classType)
            parameterModel.schema = schema
        }
        return parameterModel
    }

    private fun MethodParameter.toKParameter(): KParameter? {
        // ignore return type, see org.springframework.core.MethodParameter.getParameterIndex
        if (parameterIndex == -1) return null
        val kotlinFunction = method?.kotlinFunction ?: return null
        // The first parameter of the kotlin function is the "this" reference and not needed here.
        // See also kotlin.reflect.KCallable.getParameters
        return kotlinFunction.parameters[parameterIndex + 1]
    }

    override fun getOrder(): Int {
        return Int.MAX_VALUE
    }
}