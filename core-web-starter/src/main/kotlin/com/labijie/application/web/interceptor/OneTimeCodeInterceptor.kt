package com.labijie.application.web.interceptor

import com.labijie.application.ApplicationErrors
import com.labijie.application.component.VerifiedOneTimeCodeSourceHolder
import com.labijie.application.service.IOneTimeCodeService
import com.labijie.application.exception.InvalidOneTimeCodeException
import com.labijie.application.getOneTimeCodeInRequest
import com.labijie.application.localeErrorMessage
import com.labijie.application.web.annotation.OneTimeCodeVerify
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.server.ServletServerHttpResponse
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.http.converter.OAuth2ErrorHttpMessageConverter
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import java.lang.Exception

/**
 * @author Anders Xiao
 * @date 2025/7/31
 */
class OneTimeCodeInterceptor(
    private val oneTimeCodeService: IOneTimeCodeService
) : HandlerInterceptor {
    companion object {
        const val CODE_KEY = "ot_code"
        const val STAMP_KEY = "ot_stamp"
        val statusOnFailure = HttpStatus.FORBIDDEN
    }

    private val errorHttpMessageConverter = OAuth2ErrorHttpMessageConverter()


    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val method = (handler as? HandlerMethod);
        if (method != null) {
            val anno = method.getMethodAnnotation(OneTimeCodeVerify::class.java)
            if (anno != null) {
                val code = request.getOneTimeCodeInRequest()

                if(code == null) {
                    throw InvalidOneTimeCodeException()
                }

                val valid = oneTimeCodeService.verifyCode(code.code, code.stamp, throwIfInvalid = false)

                if (!valid.success) {
                    val err = OAuth2Error(
                        ApplicationErrors.InvalidOneTimeCode,
                        localeErrorMessage(ApplicationErrors.InvalidOneTimeCode), null
                    )

                    val serverResponse = ServletServerHttpResponse(response)
                    serverResponse.setStatusCode(statusOnFailure)
                    errorHttpMessageConverter.write(err, null, serverResponse)
                    return false
                }
                valid.target?.let {
                    source ->
                    VerifiedOneTimeCodeSourceHolder.set(valid)
                }
            }
        }
        return true
    }


    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        VerifiedOneTimeCodeSourceHolder.clear()
    }

}