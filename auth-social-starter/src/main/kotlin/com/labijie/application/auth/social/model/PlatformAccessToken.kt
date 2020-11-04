package com.labijie.application.auth.social.model

import java.time.Duration

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-10
 */
data class PlatformAccessToken(
    var userKey:String = "",
    var sessionKey:String = "",
    var sessionValue:String = "",
    var appId:String = "",
    var appOpenId:String = "",
    var expired: Duration? = null,
    var refreshToken: String? = null,
    var refreshTokenExpired: Duration? = null)