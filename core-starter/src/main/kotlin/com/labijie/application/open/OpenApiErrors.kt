package com.labijie.application.open

import com.labijie.application.Constants
import com.labijie.application.ErrorDescription

object OpenApiErrors {
    @ErrorDescription("找不到开发者账号")
    @ErrorDescription("Developer account not found", locale = Constants.EN_US)
    const val PartnerNotFound = "open_partner_not_found"

    @ErrorDescription("找不到应用")
    @ErrorDescription("Application not found", locale = Constants.EN_US)
    const val AppNotFound = "open_app_not_found"

    @ErrorDescription("签名验证失败")
    @ErrorDescription("Signature verification failed", locale = Constants.EN_US)
    const val InvalidSignature = "open_bad_signature"

    @ErrorDescription("不支持该签名算法")
    @ErrorDescription("Unsupported signature algorithm", locale = Constants.EN_US)
    const val UnsupportedSignAlgorithm = "unsupported_sign_algorithm"
}