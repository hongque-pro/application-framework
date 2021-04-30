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

    //调用第三方服务起错误
    @ErrorDescription("读取用户资料失败，服务器解密数据时发生错误")
    const val DATA_DECRYPTION_FAULT = "social_data_decryption_fault"

    //用户账号已锁定
    @ErrorDescription("用户账号已锁定")
    const val SOCIAL_USER_LOCKED = "social_user_locked"
}