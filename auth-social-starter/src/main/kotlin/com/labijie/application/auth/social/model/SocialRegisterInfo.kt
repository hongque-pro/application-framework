package com.labijie.application.auth.social.model

import com.labijie.application.validation.ConfigurablePattern
import javax.validation.constraints.NotBlank

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-11
 */
class SocialRegisterInfo {
    @get:NotBlank
    var provider:String = ""

    @get:NotBlank
    var phoneNumber:String = ""

    @get:NotBlank
    var code:String = ""

    var iv:String = ""

    var addition: String = ""
}