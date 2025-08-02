package com.labijie.application.web.interceptor

import com.labijie.application.ApplicationErrors
import com.labijie.application.component.IHumanChecker
import com.labijie.application.isEnabled
import com.labijie.application.localeErrorMessage
import com.labijie.application.web.annotation.HumanVerify
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.server.ServletServerHttpResponse
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.http.converter.OAuth2ErrorHttpMessageConverter
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-11-24
 */
class HumanVerifyInterceptor(
    private val checker: IHumanChecker
) : HandlerInterceptor {
    companion object {
        const val TOKEN_HTTP_NAME = "captcha"
        const val TOKEN_HTTP_STAMP_NAME = "captcha_stamp"
        val statusOnFailure = HttpStatus.FORBIDDEN
    }

    private val errorHttpMessageConverter = OAuth2ErrorHttpMessageConverter()


    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val method = (handler as? HandlerMethod);
        if (method != null && checker.isEnabled) {
            val anno = method.getMethodAnnotation(HumanVerify::class.java)
            if (anno != null) {
                val token = request.getHeader(TOKEN_HTTP_NAME) ?: request.getParameter(TOKEN_HTTP_NAME).orEmpty()
                val tokenStamp = request.getHeader(TOKEN_HTTP_STAMP_NAME) ?: request.getParameter(TOKEN_HTTP_STAMP_NAME)

                var valid = false
                if (token.isNotBlank() && (!tokenStamp.isNullOrBlank() || !checker.clientStampRequired())) {
                    valid = checker.check(token, tokenStamp, request.remoteAddr)
                }

                if (!valid) {
                    val err = OAuth2Error(
                        ApplicationErrors.RobotDetected,
                        localeErrorMessage(ApplicationErrors.RobotDetected), null
                    )

                    val serverResponse = ServletServerHttpResponse(response)
                    serverResponse.setStatusCode(statusOnFailure)
                    errorHttpMessageConverter.write(err, null, serverResponse)
                    return false
                }
            }
        }
        return true
    }

}