package com.labijie.application.aliyun.model

import javax.validation.constraints.NotBlank

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-11-23
 */
data class AfsRequest(
    @NotBlank
    var token:String = "",
    @NotBlank
    var sig: String = "",
    @NotBlank
    var sessionId: String = "",
    @NotBlank
    var scene: String = "",
    @NotBlank
    var appKey: String = "",
    
    var remoteIp:String = ""
)