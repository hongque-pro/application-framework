package com.labijie.application.auth

import com.labijie.application.Constants
import com.labijie.application.ErrorDescription
import com.labijie.infra.oauth2.client.OAuth2ClientErrorCodes
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

    @ErrorDescription("客户端身份认证失败或请求中提供的 client 资料无效。")
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

    @ErrorDescription("第三方登录授权信息已过期")
    @ErrorDescription("Third-party login user info expired.", locale = Constants.EN_US)
    const val EXPIRED_OAUTH2_USER_TOKEN = "expired_oauth2_user_token"

    @ErrorDescription("OAuth2 授权方不可用")
    @ErrorDescription("OAuth2 client registration is unavailable", locale = Constants.EN_US)
    const val INVALID_OAUTH2_CLIENT_REGISTRATION = "invalid_oauth2_client_registration"

    @ErrorDescription("DPoP 证明（JWT 令牌）格式无效、结构错误、签名错误，或者不符合规范要求")
    @ErrorDescription("The DPoP Proof JWT is invalid", locale = Constants.EN_US)
    const val INVALID_DPOP_PROOF: String = OAuth2ErrorCodes.INVALID_DPOP_PROOF

    @ErrorDescription("第三方提供登录提供程序不支持或被禁用")
    @ErrorDescription("The OAuth2 client references a provider that is unsupported", locale = Constants.EN_US)
    const val INVALID_OAUTH2_CLIENT_PROVIDER: String = OAuth2ClientErrorCodes.INVALID_OAUTH2_CLIENT_PROVIDER

    @ErrorDescription("OpenID Connect 令牌无效")
    @ErrorDescription("Invalid oidc token， the provided ID Token is malformed, expired, or failed signature verification", locale = Constants.EN_US)
    const val INVALID_OIDC_TOKEN: String = OAuth2ClientErrorCodes.INVALID_OIDC_TOKEN

    @ErrorDescription("客户端尝试从授权服务器获取或解析 Token 时，收到了一个无效或格式错误的响应")
    @ErrorDescription("Failed to parse the token response from the OAuth2 authorization server", locale = Constants.EN_US)
    const val INVALID_TOKEN_RESPONSE_ERROR_CODE: String = OAuth2ClientErrorCodes.INVALID_TOKEN_RESPONSE_ERROR_CODE

    @ErrorDescription("第三方登录用户尚未在系统中绑定账号或被允许登录")
    @ErrorDescription("The third-party account has no corresponding user in the system", locale = Constants.EN_US)
    const val OAUTH2_USER_NOT_REGISTERED: String = OAuth2ClientErrorCodes.OAUTH2_ACCOUNT_NOT_REGISTERED

    @ErrorDescription("第三方账号已经被绑定到系统中的其他用户")
    @ErrorDescription("The third-party account has already been linked to another user", locale = Constants.EN_US)
    const val OAUTH2_ACCOUNT_LINKED_BY_ANOTHER: String = OAuth2ClientErrorCodes.OAUTH2_ACCOUNT_LINKED_BY_ANOTHER
}
