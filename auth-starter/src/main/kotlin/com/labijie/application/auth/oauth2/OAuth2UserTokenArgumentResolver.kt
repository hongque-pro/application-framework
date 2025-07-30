/**
 * @author Anders Xiao
 * @date 2024-06-13
 */
package com.labijie.application.auth.oauth2

import com.labijie.application.SpringContext
import com.labijie.application.auth.exception.InvalidOAuth2UserTokenException
import com.labijie.application.auth.service.IOAuth2UserTokenCodec
import com.labijie.infra.oauth2.client.StandardOidcUser
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer


class OAuth2UserTokenArgumentResolver : HandlerMethodArgumentResolver {

    companion object {
        const val TOKEN_PARAMETER_NAME = "oauth2_token"
    }

    private val oauth2UserTokenCodec : IOAuth2UserTokenCodec by lazy {
        SpringContext.current.getBean(IOAuth2UserTokenCodec::class.java)
    }

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        val type = parameter.parameterType
        return StandardOidcUser::class.java == type || OAuth2UserTokenAndValue::class.java == type
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val containValue = parameter.parameterType == OAuth2UserTokenAndValue::class.java

        val tokenValue = webRequest.getParameter(TOKEN_PARAMETER_NAME) ?: webRequest.getHeader(TOKEN_PARAMETER_NAME)
        if (parameter.isOptional && tokenValue.isNullOrBlank()) {
            return null
        }

        if(tokenValue.isBlank()) {
            throw InvalidOAuth2UserTokenException()
        }

        val token = oauth2UserTokenCodec.decode(tokenValue ?: "", check = true)

        return if(containValue) OAuth2UserTokenAndValue(token, tokenValue ?: "") else token
    }
}