package com.labijie.application.identity.social

import com.labijie.application.identity.model.PlatformAccessToken

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-10
 */
interface ILoginProviderPhoneNumberSupport {
    fun decryptPhoneNumber(encryptedData:String, token: PlatformAccessToken, iv:String? = null): String
}