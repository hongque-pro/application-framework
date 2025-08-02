package com.labijie.application.web.interceptor

import com.labijie.application.component.VerifiedOneTimeCodeSourceHolder
import com.labijie.application.model.OneTimeCodeTarget
import com.labijie.application.model.OneTimeCodeVerifyResult
import com.labijie.application.web.annotation.OneTimeCodeVerify
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

/**
 * @author Anders Xiao
 * @date 2025/7/31
 */
class OneTimeCodeVerifyArgumentResolver : HandlerMethodArgumentResolver {


    override fun supportsParameter(parameter: MethodParameter): Boolean {
        val type = parameter.parameterType
        val result = (OneTimeCodeTarget::class.java == type || OneTimeCodeVerifyResult::class.java == type)
                && parameter.method?.getAnnotation(
            OneTimeCodeVerify::class.java
        ) != null

        return result
    }


    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val type = parameter.parameterType
//        if(OneTimeCodeVerifyRequest::class.java == type) {
//            val code = webRequest.getParameter(OneTimeCodeInterceptor.CODE_KEY) ?: webRequest.getHeader(OneTimeCodeInterceptor.CODE_KEY)
//            val header = webRequest.getParameter(OneTimeCodeInterceptor.STAMP_KEY) ?: webRequest.getHeader(OneTimeCodeInterceptor.STAMP_KEY)
//            return OneTimeCodeVerifyRequest(code.orEmpty(), header.orEmpty()).let {
//                BeanValidator.validate(it)
//            }
//        }

        if(OneTimeCodeTarget::class.java == type) {
            return VerifiedOneTimeCodeSourceHolder.get()?.target
        }

        return VerifiedOneTimeCodeSourceHolder.get()
    }
}