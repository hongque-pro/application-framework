/**
 * @author Anders Xiao
 * @date 2024-06-14
 */
package com.labijie.application.auth.component

import com.labijie.application.auth.oauth2.OAuth2UserToken
import com.labijie.application.auth.oauth2.OAuth2UserTokenArgumentResolver
import com.labijie.application.doc.DocUtils
import io.swagger.v3.oas.models.media.StringSchema
import io.swagger.v3.oas.models.parameters.Parameter
import org.springdoc.core.customizers.ParameterCustomizer
import org.springframework.core.MethodParameter


class OAuth2UserDocParameterCustomizer : ParameterCustomizer {
//    override fun customize(operation: Operation, handlerMethod: HandlerMethod): Operation {
//        handlerMethod.method.parameters.forEach {
//            if(it.type.equals(OAuth2UserToken::class.java)) {
//                operation.addParametersItem(HeaderParameter().name(OAuth2UserTokenArgumentResolver.TOKEN_PARAMETER_NAME).schema(StringSchema()))
//                operation.addParametersItem(QueryParameter().name(OAuth2UserTokenArgumentResolver.TOKEN_PARAMETER_NAME).schema(StringSchema()))
//            }
//        }
//        return operation
//    }

    override fun customize(parameterModel: Parameter?, methodParameter: MethodParameter?): Parameter? {
        if(methodParameter != null && parameterModel != null && methodParameter.parameter.type.equals(OAuth2UserToken::class.java)) {
            parameterModel.schema = StringSchema()
            parameterModel.name(OAuth2UserTokenArgumentResolver.TOKEN_PARAMETER_NAME)
            parameterModel.required(DocUtils.isMethodParameterRequired(methodParameter))
            parameterModel.description("Support for assigning values from http headers.")
        }
        return parameterModel
    }
}