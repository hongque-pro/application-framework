package com.labijie.application.aliyun.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-11-23
 */
class AfsResult {
    @JsonProperty("Code")
    var code: String = ""
    val success: Boolean
        get() = code == "100"
}