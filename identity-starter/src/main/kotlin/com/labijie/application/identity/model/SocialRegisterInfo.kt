package com.labijie.application.identity.model

import javax.validation.constraints.Max
import javax.validation.constraints.NotBlank

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-11
 */
class SocialRegisterInfo {
    @get:Max(32)
    var username: String? = null

    @get:NotBlank
    @get:Max(32)
    var provider:String = ""

    @get:NotBlank
    @get:Max(16)
    var phoneNumber:String = ""

    @get:NotBlank
    var code:String = ""

    /**
     * 用于解密手机号的 IV （如果为空，表示是明文手机号）
     */
    var iv:String? = null

    var addition: String = ""
}