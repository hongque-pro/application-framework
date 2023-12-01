package com.labijie.application.auth.component

import com.labijie.application.web.isFromAlipay
import com.labijie.application.web.isFromWechat
import jakarta.servlet.http.HttpServletRequest

interface ISignInPlatformDetection {
    fun detect(request: HttpServletRequest): String {
        return when {
            request.isFromAlipay -> "alipay"
            request.isFromWechat -> "wechat"
            else -> "web"
        }
    }
}