package com.labijie.application.auth.social.abstraction

import com.labijie.application.auth.social.SocialAuthOptions
import com.labijie.application.identity.model.PlatformAccessToken
import com.labijie.application.identity.social.ILoginProviderPhoneNumberSupport

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-10
 */
abstract class AbstractMiniProgramProvider<TOptions: SocialAuthOptions>(options: TOptions) :
    AbstractLoginProvider<TOptions>(options), ILoginProviderPhoneNumberSupport {

    override val isMultiOpenId: Boolean
        get() = options.multiOpenId

    override fun decryptPhoneNumber(encryptedData: String, token: PlatformAccessToken, iv: String?): String {
        if(this.options.isSandbox){
            return this.options.sandboxPhoneNumber
        }
        return decryptTurelyPhoneNumber(encryptedData, token, iv)
    }

    abstract fun decryptTurelyPhoneNumber(encryptedData: String, token: PlatformAccessToken, iv: String?): String
}