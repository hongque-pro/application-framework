package com.labijie.application.web.interceptor

import com.labijie.application.component.VerifiedOneTimeCodeSourceHolder
import com.labijie.application.exception.InvalidOneTimeCodeException
import com.labijie.application.getOneTimeCodeInRequest
import com.labijie.application.service.IOneTimeCodeService
import com.labijie.application.web.annotation.OneTimeCodeVerify
import com.labijie.infra.utils.throwIfNecessary
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

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

        val logger by lazy {
            LoggerFactory.getLogger(OneTimeCodeInterceptor::class.java)
        }
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val method = (handler as? HandlerMethod);
        if (method != null) {
            val anno = method.getMethodAnnotation(OneTimeCodeVerify::class.java)
            if (anno != null) {
                val code = request.getOneTimeCodeInRequest()

                if(code == null) {
                    if(logger.isDebugEnabled) {
                        logger.warn("Unable to got one-time code or stamp.")
                    }
                    throw InvalidOneTimeCodeException(InvalidOneTimeCodeException.REASON_MISS_REQUEST_PARAM)
                }

                val valid =  try {
                    oneTimeCodeService.verifyCode(code.code, code.stamp, throwIfInvalid = true)
                }catch (e: Throwable){
                    e.throwIfNecessary()
                    logger.error("IOneTimeCodeService is expected is to return true /false, but got throw exception.", e)
                    throw InvalidOneTimeCodeException(cause = e)
                }

                if (!valid.success) {
                    throw InvalidOneTimeCodeException()
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