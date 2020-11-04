package com.labijie.application.auth.social

import com.labijie.application.auth.social.model.PlatformAccessToken

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-10
 */
interface IMiniProgramProvider : ILoginProvider {
    fun decryptPhoneNumber(encryptedData:String, token:PlatformAccessToken, iv:String? = null): String
}