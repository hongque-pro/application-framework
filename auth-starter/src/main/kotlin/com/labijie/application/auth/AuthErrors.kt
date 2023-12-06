package com.labijie.application.auth

import com.labijie.application.ErrorDescription
import org.springframework.security.oauth2.core.OAuth2ErrorCodes

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-05
 */
object AuthErrors {
    @ErrorDescription("错误的请求格式")
    const val INVALID_REQUEST = OAuth2ErrorCodes.INVALID_REQUEST
    @ErrorDescription("客户端认证失败")
    const val INVALID_CLIENT = OAuth2ErrorCodes.INVALID_CLIENT
    @ErrorDescription("用户名或密码不正确")
    const val INVALID_GRANT = OAuth2ErrorCodes.INVALID_GRANT
    @ErrorDescription("未授权的客户端访问请求")
    const val UNAUTHORIZED_CLIENT = OAuth2ErrorCodes.UNAUTHORIZED_CLIENT
    @ErrorDescription("不支持该授权类型, 请检查 grant type 是否正确")
    const val UNSUPPORTED_GRANT_TYPE = OAuth2ErrorCodes.UNSUPPORTED_GRANT_TYPE
    @ErrorDescription("授权请求的范围无效, 请检查 scope 是否正确")
    const val INVALID_SCOPE = OAuth2ErrorCodes.INVALID_SCOPE
    @ErrorDescription("授权的范围不足以完成该操作")
    const val INSUFFICIENT_SCOPE = OAuth2ErrorCodes.INSUFFICIENT_SCOPE
    @ErrorDescription("客户端令牌不正确或已过期，请重试或重新登录")
    const val INVALID_TOKEN = OAuth2ErrorCodes.INVALID_TOKEN
    @ErrorDescription("回调地址不正确")
    const val INVALID_REDIRECT_URI = OAuth2ErrorCodes.INVALID_REDIRECT_URI
    @ErrorDescription("服务器不支持该响应类型")
    const val UNSUPPORTED_RESPONSE_TYPE = OAuth2ErrorCodes.UNSUPPORTED_RESPONSE_TYPE
    @ErrorDescription("你没有权限访问该资源")
    const val ACCESS_DENIED = OAuth2ErrorCodes.ACCESS_DENIED

    @ErrorDescription("账号已锁定")
    const val ACCOUNT_LOCKED = "account_locked"


}