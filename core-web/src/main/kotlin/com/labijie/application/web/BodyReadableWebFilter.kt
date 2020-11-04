package com.labijie.application.web

import javax.servlet.*
import javax.servlet.http.HttpServletRequest

/**
 * 通过缓存 body 解决 body 读两次的问题
 */
abstract class BodyReadableWebFilter : Filter {
    override fun destroy() {
    }

    override fun init(filterConfig: FilterConfig?) {

    }

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val r = request as HttpServletRequest
        if(r.method.equals("GET", ignoreCase = true) || r.contentLength == 0){
            chain?.doFilter(request, response)
        }else {
            val requestWrapper = BodyCachedHttpServletRequest(r)
            chain?.doFilter(requestWrapper, response)
        }
    }
}