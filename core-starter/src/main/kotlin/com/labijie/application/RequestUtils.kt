package com.labijie.application


/**
 * @author Anders Xiao
 * @date 2025/7/11
 */
object RequestUtils {

    fun isDefaultPort(scheme: String?, port: Int): Boolean {
        return ("http".equals(scheme, ignoreCase = true) && port == 80)
                || ("https".equals(scheme, ignoreCase = true) && port == 443)
    }
}