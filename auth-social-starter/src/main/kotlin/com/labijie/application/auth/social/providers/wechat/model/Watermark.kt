package com.labijie.application.auth.social.providers.wechat.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-14
 */
data class Watermark(
    @get:JsonProperty("appid")
    var appId: String = "",
    @get:JsonProperty("timestamp")
    var timestamp: String = ""
)