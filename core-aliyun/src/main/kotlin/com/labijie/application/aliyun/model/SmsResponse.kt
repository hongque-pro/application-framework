package com.labijie.application.aliyun.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-11-18
 */
class SmsResponse {
    @JsonProperty("BizId")
    var bizId:String = ""

    @JsonProperty("Code")
    var code:String = ""

    @JsonProperty("Message")
    var message: String = ""

    @JsonProperty("requestId")
    var requestId:String = ""
}