package com.labijie.application.web.interceptor

import com.labijie.application.ApplicationErrors
import com.labijie.application.component.IHumanChecker
import com.labijie.application.web.annotation.HumanVerify
import com.labijie.application.web.getRealIp
import com.labijie.application.web.handler.ErrorResponse
import com.labijie.infra.json.JacksonHelper
import org.springframework.http.HttpStatus
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-11-24
 */
class HumanVerifyInterceptor(private val checker: IHumanChecker) : HandlerInterceptor {
    companion object {
        const val TOKEN_HTTP_HEADER_NAME = "h-token"
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val method = (handler as? HandlerMethod);
        if (method != null) {
            val anno = method.getMethodAnnotation(HumanVerify::class.java)
            if (anno != null && !intercept(checker, request)) {
                response.status = HttpStatus.FORBIDDEN.value()
                val resp = ErrorResponse(
                    ApplicationErrors.RobotDetected,
                    "robot detected."
                )
                response.writer.write(JacksonHelper.serializeAsString(resp))
                response.writer.flush()
                response.writer.close()
                return false
            }
        }
        return true
    }

    private fun intercept(
        checker: IHumanChecker,
        request: HttpServletRequest
    ): Boolean {
        val token = request.getHeader(TOKEN_HTTP_HEADER_NAME).orEmpty()
        val clientIp = request.getRealIp()
        return checker.check(token, clientIp)
    }


}