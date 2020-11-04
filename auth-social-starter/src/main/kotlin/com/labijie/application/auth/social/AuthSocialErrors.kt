package com.labijie.application.auth.social

import com.labijie.application.ErrorDescription

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-11
 */
object AuthSocialErrors {
    @ErrorDescription("调用第三方服务发生错误")
    const val SOCIAL_EXCHANGE_FAULT = "social_exchange_fault"

    //不支持登录该平台（服务器可能关闭了该平台的登录入口）
    @ErrorDescription("不支持该登录方式, 服务器可能关闭了该渠道的登录入口")
    const val UNSUPPORTED_LOGIN_PROVIDER = "social_invalid_login_provider"

    //调用第三方服务起错误
    @ErrorDescription("读取用户资料失败，服务器解密数据时发生错误")
    const val DATA_DECRYPTION_FAULT = "social_data_decryption_fault"

    //用户账号已锁定
    @ErrorDescription("用户账号已锁定")
    const val SOCIAL_USER_LOCKED = "social_user_locked"
}