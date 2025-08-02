package com.labijie.application.identity

import com.labijie.application.Constants
import com.labijie.application.ErrorDescription

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-05
 */
object IdentityErrors {

    @ErrorDescription("手机号已经被注册")
    @ErrorDescription("Phone number is already registered", locale = Constants.EN_US)
    const val PHONE_ALREADY_EXISTED = "phone_number_already_existed"

    @ErrorDescription("邮件地址已经被注册")
    @ErrorDescription("Email address is already registered", locale = Constants.EN_US)
    const val EMAIL_ALREADY_EXISTED = "email_already_existed"

    @ErrorDescription("用户账号已存在")
    @ErrorDescription("User account already exists", locale = Constants.EN_US)
    const val USER_ALREADY_EXISTED = "user_already_existed"

    @ErrorDescription("找不到该角色")
    @ErrorDescription("Role not found", locale = Constants.EN_US)
    const val ROLE_NOT_FOUND = "role_not_found"

    @ErrorDescription("密码验证失败，请检查输入的密码是否正确")
    @ErrorDescription("Password verification failed, please check if the input password is correct", locale = Constants.EN_US)
    const val INVALID_PASSWORD = "invalid_password"

    @ErrorDescription("两次输入的密码不一致")
    @ErrorDescription("Passwords do not match", locale = Constants.EN_US)
    const val INVALID_REPEAT_PWD = "invalid_repeat_pwd"

    @ErrorDescription("账号已锁定")
    @ErrorDescription("Account has been locked", locale = Constants.EN_US)
    const val ACCOUNT_LOCKED = "account_locked"


}
