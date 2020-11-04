package com.labijie.application.aliyun.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-20
 */
@ConfigurationProperties("aliyun")
class AliyunProperties {
    var oss:OssSettings = OssSettings()
    var sts: StsSettings = StsSettings()
    var sms: SmsSettings = SmsSettings()
    var afs: AfsSettings =
        AfsSettings()
    var accessKeyId :String = ""
    var accessKeySecret  :String = ""
}