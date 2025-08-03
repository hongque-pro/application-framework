package com.labijie.application.auth.doc

import com.labijie.application.auth.annotation.ServerIdToken
import io.swagger.v3.oas.models.parameters.Parameter
import org.springdoc.core.customizers.ParameterCustomizer
import org.springframework.core.MethodParameter

/**
 * @author Anders Xiao
 * @date 2024-06-14
 */
class ServerIdTokenParameterCustomizer : ParameterCustomizer {

    override fun customize(parameterModel: Parameter?, methodParameter: MethodParameter?): Parameter? {

        if(methodParameter != null && methodParameter.parameter.getAnnotation(ServerIdToken::class.java) != null)
        {
            return null
        }
        return parameterModel
    }
}