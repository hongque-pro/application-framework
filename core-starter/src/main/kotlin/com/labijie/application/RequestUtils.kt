package com.labijie.application

import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes


/**
 * @author Anders Xiao
 * @date 2025/7/11
 */
object RequestUtils {
    fun getServerBaseUrl(): String? {
        val attrs = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?
        if (attrs == null) {
            return null // 当前线程没有请求上下文
        }

        val request = attrs.request
        if (request == null) {
            return null
        }

        // 方法二：从 request 中组合 host 和 port
        val scheme = request.scheme // http 或 https
        val serverName = request.serverName // 服务器名称或 IP
        val serverPort = request.serverPort // 端口
        return scheme + "://" + serverName + (if (isDefaultPort(scheme, serverPort)) "" else ":$serverPort")
    }

    private fun isDefaultPort(scheme: String?, port: Int): Boolean {
        return ("http".equals(scheme, ignoreCase = true) && port == 80)
                || ("https".equals(scheme, ignoreCase = true) && port == 443)
    }
}