package com.labijie.application.identity.exception

import com.labijie.application.identity.IdentityErrors

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-11
 */
class UnsupportedLoginProviderException(providerName:String, message: String? = null) :
        IdentitySocialException(providerName, IdentityErrors.UNSUPPORTED_LOGIN_PROVIDER, message ?: "unsupported login provider")