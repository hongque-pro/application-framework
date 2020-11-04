package com.labijie.application.auth

import javax.servlet.http.HttpServletRequest

/**
 * @author <a href="http://github.com/saintdan">Liao Yifan</a>
 * @date 18/12/2017
 * @since JDK1.8
 */
object RemoteAddressUtils {
    fun getRealIp(request: HttpServletRequest): String {
        return getHeaderValue(request, "X-Forwarded-For") ?:
                getHeaderValue(request, "X-Real-IP") ?:
                getHeaderValue(request, "WL-Proxy-Client-IP") ?:
                request.remoteAddr
    }

    private fun getHeaderValue(request: HttpServletRequest, name:String): String? {
        val headValue = request.getHeader(name) //squid
        return if (!headValue.isNullOrBlank() && !"unKnown".equals(headValue, ignoreCase = true)) {
            headValue.getFirstValue()
        } else null
    }

    private fun String.getFirstValue(): String {
        val index = this.indexOf(",")
        return if (index != -1) {
            this.substring(0, index)
        } else {
            this
        }
    }
}