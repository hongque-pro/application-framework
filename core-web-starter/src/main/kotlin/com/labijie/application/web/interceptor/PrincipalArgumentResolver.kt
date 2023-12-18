package com.labijie.application.web.interceptor

import com.labijie.infra.oauth2.OAuth2Utils
import com.labijie.infra.oauth2.TwoFactorPrincipal
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-06
 */
class PrincipalArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        val type = parameter.parameterType
        return TwoFactorPrincipal::class.java == type
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        if(parameter.isOptional){
            return OAuth2Utils.currentTwoFactorPrincipalOrNull()
        }
        return OAuth2Utils.currentTwoFactorPrincipal()
    }
}