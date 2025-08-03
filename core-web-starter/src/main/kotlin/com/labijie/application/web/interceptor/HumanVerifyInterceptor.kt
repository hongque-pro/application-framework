package com.labijie.application.web.interceptor

import com.labijie.application.component.IHumanChecker
import com.labijie.application.exception.RobotDetectedException
import com.labijie.application.isEnabled
import com.labijie.application.web.annotation.HumanVerify
import com.labijie.infra.utils.throwIfNecessary
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
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
        const val TOKEN_KEY = "captcha"
        const val STAMP_KEY = "captcha_stamp"
        private val logger by lazy {
            LoggerFactory.getLogger(HumanVerifyInterceptor::class.java)
        }
    }
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {

        val method = (handler as? HandlerMethod);
        if (method != null && checker.isEnabled) {
            val anno = method.getMethodAnnotation(HumanVerify::class.java)
            if (anno != null) {
                val token = request.getHeader(TOKEN_KEY) ?: request.getParameter(TOKEN_KEY).orEmpty()
                val tokenStamp = request.getHeader(STAMP_KEY) ?: request.getParameter(STAMP_KEY)

                if (token.isNotBlank() && (!tokenStamp.isNullOrBlank() || !checker.clientStampRequired())) {
                    try {
                        if(!checker.check(token, tokenStamp, request.remoteAddr))
                        {
                            throw RobotDetectedException()
                        }
                    }catch (re: RobotDetectedException) {
                        throw re
                    }
                    catch (e: Throwable) {
                        e.throwIfNecessary()
                        logger.error("Human checker is expected is to return true /false, but got throw exception.", e)
                        throw RobotDetectedException()
                    }
                }
            }
        }
        return true
    }

}