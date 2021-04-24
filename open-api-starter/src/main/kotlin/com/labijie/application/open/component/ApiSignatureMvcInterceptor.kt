package com.labijie.application.open.component

import com.labijie.application.open.OpenApiRequest
import com.labijie.application.open.OpenSignatureUtils
import com.labijie.application.open.annotation.IgnoreApiSignature
import com.labijie.application.open.exception.AppNotFoundException
import com.labijie.application.open.service.IOpenAppService
import com.labijie.application.web.getBody
import com.labijie.application.web.hasAnnotationOnMethodOrClass
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ApiSignatureMvcInterceptor(private val appService: IOpenAppService) : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val method = (handler as? HandlerMethod)
        if (method?.hasAnnotationOnMethodOrClass(IgnoreApiSignature::class) != true) {
            val resq = OpenApiRequest(request.queryString, request.getBody().readBytes().toString(Charsets.UTF_8))
            val (_, data) = resq.signAndData
            OpenSignatureUtils.validateParameters(data)

            val key = appService.getSecret(resq.appId.toLong()) ?: throw AppNotFoundException()
            OpenSignatureUtils.verifySign(resq, key.key, key.algorithm)
        }
        return true
    }
}