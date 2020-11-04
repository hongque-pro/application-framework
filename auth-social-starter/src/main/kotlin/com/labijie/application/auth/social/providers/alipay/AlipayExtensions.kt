package com.labijie.application.auth.social.providers.alipay

import com.labijie.application.crypto.RsaUtils
import com.labijie.infra.utils.nowString
import java.time.ZoneOffset

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-13
 */
fun  AlipayMiniOptions.buildCommonsParam(method: String, appOAuthToken:String? = null): MutableMap<String, String> {
    val map = mutableMapOf(
        "app_id" to this.appId,
        "method" to method,
        "format" to "json",
        "charset" to "UTF-8",
        "timestamp" to nowString(ZoneOffset.ofHours(8)),
        "version" to "1.0"
    )
    if(!appOAuthToken.isNullOrBlank()){
        map["app_auth_token"] = appOAuthToken
    }
    return map
}

fun AlipayMiniOptions.signParameters(params : MutableMap<String, String>) {
    params["sign_type"] = "RSA2"
    val sign = RsaUtils.rsaSignSHA256(params, this.appSecret)
    params["sign"] = sign
}