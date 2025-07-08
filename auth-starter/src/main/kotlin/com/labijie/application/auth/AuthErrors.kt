package com.labijie.application.auth

import com.labijie.application.Constants
import com.labijie.application.ErrorDescription
import org.springframework.security.oauth2.core.OAuth2ErrorCodes

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-05
 */
object AuthErrors {
    @ErrorDescription("错误的请求格式")
    @ErrorDescription("Invalid request format", locale = Constants.EN_US)
    const val INVALID_REQUEST = OAuth2ErrorCodes.INVALID_REQUEST

    @ErrorDescription("客户端认证失败")
    @ErrorDescription("Client authentication failed", locale = Constants.EN_US)
    const val INVALID_CLIENT = OAuth2ErrorCodes.INVALID_CLIENT

    @ErrorDescription("用户名或密码不正确")
    @ErrorDescription("Invalid username or password", locale = Constants.EN_US)
    const val INVALID_GRANT = OAuth2ErrorCodes.INVALID_GRANT

    @ErrorDescription("未授权的客户端访问请求")
    @ErrorDescription("Unauthorized client access request", locale = Constants.EN_US)
    const val UNAUTHORIZED_CLIENT = OAuth2ErrorCodes.UNAUTHORIZED_CLIENT

    @ErrorDescription("不支持该授权类型, 请检查 grant type 是否正确")
    @ErrorDescription("Unsupported grant type, please check the grant_type value", locale = Constants.EN_US)
    const val UNSUPPORTED_GRANT_TYPE = OAuth2ErrorCodes.UNSUPPORTED_GRANT_TYPE

    @ErrorDescription("授权请求的范围无效, 请检查 scope 是否正确")
    @ErrorDescription("Invalid scope in authorization request, please check the scope", locale = Constants.EN_US)
    const val INVALID_SCOPE = OAuth2ErrorCodes.INVALID_SCOPE

    @ErrorDescription("授权的范围不足以完成该操作")
    @ErrorDescription("Insufficient scope to complete the operation", locale = Constants.EN_US)
    const val INSUFFICIENT_SCOPE = OAuth2ErrorCodes.INSUFFICIENT_SCOPE

    @ErrorDescription("客户端令牌不正确或已过期，请重试或重新登录")
    @ErrorDescription("Invalid or expired client token, please retry or log in again", locale = Constants.EN_US)
    const val INVALID_TOKEN = OAuth2ErrorCodes.INVALID_TOKEN

    @ErrorDescription("回调地址不正确")
    @ErrorDescription("Invalid redirect URI", locale = Constants.EN_US)
    const val INVALID_REDIRECT_URI = OAuth2ErrorCodes.INVALID_REDIRECT_URI

    @ErrorDescription("服务器不支持该响应类型")
    @ErrorDescription("Unsupported response type by server", locale = Constants.EN_US)
    const val UNSUPPORTED_RESPONSE_TYPE = OAuth2ErrorCodes.UNSUPPORTED_RESPONSE_TYPE

    @ErrorDescription("你没有权限访问该资源")
    @ErrorDescription("You do not have permission to access this resource", locale = Constants.EN_US)
    const val ACCESS_DENIED = OAuth2ErrorCodes.ACCESS_DENIED

    // custom
    @ErrorDescription("第三方登录用户未授权或授权信息被篡改")
    @ErrorDescription("Third-party login user is unauthorized or authorization info has been tampered", locale = Constants.EN_US)
    const val INVALID_OAUTH2_USER_TOKEN = "invalid_oauth2_user_token"

    @ErrorDescription("OAuth2 授权方不可用")
    @ErrorDescription("OAuth2 client registration is unavailable", locale = Constants.EN_US)
    const val INVALID_OAUTH2_CLIENT_REGISTRATION = "invalid_oauth2_client_registration"
}
