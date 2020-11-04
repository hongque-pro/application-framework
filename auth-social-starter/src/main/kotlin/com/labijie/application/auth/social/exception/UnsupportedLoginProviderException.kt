package com.labijie.application.auth.social.exception

import com.labijie.application.auth.social.AuthSocialErrors

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-11
 */
class UnsupportedLoginProviderException(providerName:String, message: String? = null) :
    AuthSocialException(providerName, AuthSocialErrors.UNSUPPORTED_LOGIN_PROVIDER, message ?: "unsupported login provider")