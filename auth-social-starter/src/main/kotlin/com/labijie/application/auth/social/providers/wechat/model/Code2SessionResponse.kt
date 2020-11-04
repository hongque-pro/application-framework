package com.labijie.application.auth.social.providers.wechat.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-14
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class Code2SessionResponse {
    @get:JsonProperty("openid")
    var openId: String = ""
    @get:JsonProperty("session_key")
    var sessionKey: String = ""
    @get:JsonProperty("unionid")
    var unionId: String? = null
    @get:JsonProperty("errcode")
    var errorCode: String? = null
    @get:JsonProperty("errmsg")
    var errorMessage: String? = null
}