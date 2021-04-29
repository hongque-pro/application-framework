package com.labijie.application.identity

import com.labijie.application.ErrorDescription

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-05
 */
object IdentityErrors {

    @ErrorDescription("手机号已经被注册")
    const val PHONE_ALREADY_EXISTED = "phone_number_already_existed"
    @ErrorDescription("邮件地址已经被注册")
    const val EMAIL_ALREADY_EXISTED = "email_already_existed"
    @ErrorDescription("用户账号已存在")
    const val USER_ALREADY_EXISTED = "user_already_existed"
    @ErrorDescription("找不到该角色")
    const val ROLE_NOT_FOUND = "role_not_found"
    @ErrorDescription("密码验证失败，请检查输入的密码是否正确")
    const val INVALID_PASSWORD = "invalid_password"
    @ErrorDescription("两次输入的密码不一致")
    const val INVALID_REPEAT_PWD = "invalid_repeat_pwd"
    @ErrorDescription("账号已锁定")
    const val ACCOUNT_LOCKED = "account_locked"


    //不支持登录该平台（服务器可能关闭了该平台的登录入口）
    @ErrorDescription("不支持该登录方式, 服务器可能关闭了该渠道的登录入口")
    const val UNSUPPORTED_LOGIN_PROVIDER = "social_invalid_login_provider"

}