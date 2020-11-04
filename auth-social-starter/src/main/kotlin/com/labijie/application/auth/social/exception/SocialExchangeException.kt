package com.labijie.application.auth.social.exception

import com.labijie.application.auth.social.AuthSocialErrors
import org.springframework.http.HttpStatus
import java.lang.StringBuilder

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-11
 */
class SocialExchangeException(provider: String, message: String? = null) : AuthSocialException(
    provider,
    AuthSocialErrors.SOCIAL_EXCHANGE_FAULT,
    message ?: "Exchange with social server fault."
) {
    override val status: HttpStatus = HttpStatus.PRECONDITION_FAILED

    var platformResponse:String? = null

    override val message: String?
        get() {
            return StringBuilder(super.message.orEmpty()).also {
                if(platformResponse != null) {
                    it.appendLine("Platform Response: $platformResponse")
                }
            }.toString()
        }
}