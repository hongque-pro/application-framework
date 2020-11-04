package com.labijie.application.auth.social.providers.alipay.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-14
 */
class OAuthTokenResponse : CodedResponse() {

    @get:JsonProperty("alipay_system_oauth_token_response")
    var data: TokenData =TokenData()

    public  class TokenData
    {
        @get:JsonProperty("user_id")
        var userId: String = ""

        @get:JsonProperty("access_token")
        var asscessToken: String = ""

        @get:JsonProperty("expires_in")
        var expiresIn: String = ""

        @get:JsonProperty("refresh_token")
        var refreshToken: String = ""

        @get:JsonProperty("re_expires_in")
        var refreshTokenExpiresIn: String = ""
    }
}

