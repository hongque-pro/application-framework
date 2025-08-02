package com.labijie.application.auth.doc

import com.labijie.application.auth.annotation.ServerIdToken
import com.labijie.application.auth.oauth2.OAuth2UserTokenArgumentResolver
import com.labijie.application.doc.DocUtils
import com.labijie.infra.oauth2.StandardOidcUser
import io.swagger.v3.oas.models.media.StringSchema
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