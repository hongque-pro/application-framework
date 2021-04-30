package com.labijie.application.identity.social

import com.labijie.application.identity.model.PlatformAccessToken


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