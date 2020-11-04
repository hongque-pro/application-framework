package com.labijie.application.web.interceptor

import com.labijie.application.web.annotation.HttpCache
import org.springframework.http.CacheControl
import org.springframework.http.HttpHeaders
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.web.method.HandlerMethod


/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-21
 */
object HttpCacheInterceptorAdapter : HandlerInterceptorAdapter() {
    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?
    ) {
        val method = (handler as? HandlerMethod);
        if (method != null) {
            val anno = method.getMethodAnnotation(HttpCache::class.java)
            if (anno != null) {
                val value = CacheControl.maxAge(anno.maxAge, anno.unit).headerValue!!
                response.setHeader(HttpHeaders.CACHE_CONTROL, value);
            }
        }
    }
}