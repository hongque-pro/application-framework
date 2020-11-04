package com.labijie.application.aliyun.configuration

import java.time.Duration

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-20
 */
class StsSettings {
    var endpoint:String = "sts.aliyuncs.com"
    var role:String = ""
    var tokenTimeout:Duration = Duration.ofSeconds(900)
}