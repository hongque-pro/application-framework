package com.labijie.application.web.interceptor

import com.labijie.application.web.annotation.HttpCache
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.CacheControl
import org.springframework.http.HttpHeaders
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor


/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-21
 */
object HttpCacheInterceptor : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val method = (handler as? HandlerMethod);
        if (method != null) {
            val anno = method.getMethodAnnotation(HttpCache::class.java)
            if (anno != null) {
                val value = CacheControl.maxAge(anno.maxAge, anno.unit).headerValue!!
                response.setHeader(HttpHeaders.CACHE_CONTROL, value);
            }
        }
        return super.preHandle(request, response, handler)
    }
}