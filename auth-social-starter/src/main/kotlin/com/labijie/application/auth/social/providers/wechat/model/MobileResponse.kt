package com.labijie.application.auth.social.providers.wechat.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-14
 */
class MobileResponse {
    var phoneNumber: String = ""
    var purePhoneNumber: String = ""
    var countryCode: String = ""
    var watermark: Watermark = Watermark()
}