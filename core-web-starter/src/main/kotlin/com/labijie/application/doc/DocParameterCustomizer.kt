package com.labijie.application.doc

import com.labijie.application.doc.DocUtils.getEnumSchema
import io.swagger.v3.oas.models.parameters.Parameter
import org.springdoc.core.customizers.ParameterCustomizer
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component

/**
 * @author Anders Xiao
 * @date 2025/8/3
 */
@Component
class DocParameterCustomizer : ParameterCustomizer {
    override fun customize(
        parameterModel: Parameter?,
        methodParameter: MethodParameter?
    ): Parameter? {
        if(parameterModel != null && methodParameter != null) {
            val type = methodParameter.parameter.parameterizedType
            if (DocUtils.isEnumType(type)) {
                val schema = getEnumSchema(parameterModel.schema, type)
                parameterModel.description = schema.description
                parameterModel.schema = schema
            }
        }
        return parameterModel
    }

}