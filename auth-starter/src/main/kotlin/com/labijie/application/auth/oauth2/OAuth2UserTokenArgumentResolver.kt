/**
 * @author Anders Xiao
 * @date 2024-06-13
 */
package com.labijie.application.auth.oauth2

import com.labijie.application.auth.annotation.ServerIdToken
import com.labijie.infra.oauth2.StandardOidcUser
import com.labijie.infra.oauth2.exception.InvalidIdTokenException
import com.labijie.infra.oauth2.service.IOAuth2ServerOidcTokenService
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer


class OAuth2UserTokenArgumentResolver(
    private val serverOidcTokenService: IOAuth2ServerOidcTokenService
) : HandlerMethodArgumentResolver {

    companion object {
        const val ID_TOKEN_KEY = "id-token"
    }

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        val type = parameter.parameterType
        return (StandardOidcUser::class.java == type || String::class.java == type) &&
                parameter.hasParameterAnnotation(
                    ServerIdToken::class.java
                )
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {

        val annotation = parameter.getParameterAnnotation(ServerIdToken::class.java)
        if(annotation == null) {
            return null
        }

        val tokenValue = webRequest.getParameter(ID_TOKEN_KEY) ?: webRequest.getHeader(ID_TOKEN_KEY)
        val optional = parameter.isOptional || (!annotation.required)

        if (optional && tokenValue.isNullOrBlank()) {
            return null
        }

        if (tokenValue.isBlank()) {
            throw InvalidIdTokenException()
        }

        if(parameter.parameterType == String::class.java) {
            return tokenValue
        }

        return serverOidcTokenService.decode(tokenValue, annotation.ignoreExpiration)
    }
}