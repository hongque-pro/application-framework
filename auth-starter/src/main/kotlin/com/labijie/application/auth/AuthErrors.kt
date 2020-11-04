package com.labijie.application.auth

import com.labijie.application.ErrorDescription
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-05
 */
object AuthErrors {
    @ErrorDescription("请求地址不正确")
    const val URI = OAuth2Exception.URI
    @ErrorDescription("错误的请求格式")
    const val INVALID_REQUEST = OAuth2Exception.INVALID_REQUEST
    @ErrorDescription("客户端认证失败")
    const val INVALID_CLIENT = OAuth2Exception.INVALID_CLIENT
    @ErrorDescription("用户名或密码不正确")
    const val INVALID_GRANT = OAuth2Exception.INVALID_GRANT
    @ErrorDescription("客户端没有权限访问该资源")
    const val UNAUTHORIZED_CLIENT = OAuth2Exception.UNAUTHORIZED_CLIENT
    @ErrorDescription("不支持该授权类型, 请检查 grant type 是否正确")
    const val UNSUPPORTED_GRANT_TYPE = OAuth2Exception.UNSUPPORTED_GRANT_TYPE
    @ErrorDescription("授权请求的范围无效, 请检查 scope 是否正确")
    const val INVALID_SCOPE = OAuth2Exception.INVALID_SCOPE
    @ErrorDescription("授权的范围不足以完成该操作")
    const val INSUFFICIENT_SCOPE = OAuth2Exception.INSUFFICIENT_SCOPE
    @ErrorDescription("客户端令牌不正确或已过期，请重试或重新登录")
    const val INVALID_TOKEN = OAuth2Exception.INVALID_TOKEN
    @ErrorDescription("请求缺少回调地址")
    const val REDIRECT_URI_MISMATCH = OAuth2Exception.REDIRECT_URI_MISMATCH
    @ErrorDescription("服务器不支持该响应类型")
    const val UNSUPPORTED_RESPONSE_TYPE = OAuth2Exception.UNSUPPORTED_RESPONSE_TYPE
    @ErrorDescription("你没有权限访问该资源")
    const val ACCESS_DENIED = OAuth2Exception.ACCESS_DENIED

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


}