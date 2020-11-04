package com.labijie.application.auth.social

import com.labijie.application.auth.social.model.PlatformAccessToken

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-10
 */
interface ILoginProvider {
    val name:String
    val isMultiOpenId:Boolean
    fun exchangeToken(authorizationCode: String): PlatformAccessToken
}