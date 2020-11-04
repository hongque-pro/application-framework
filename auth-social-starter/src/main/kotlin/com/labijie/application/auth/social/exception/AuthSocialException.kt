package com.labijie.application.auth.social.exception

import com.labijie.application.ErrorCodedStatusException

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-11
 */
open class AuthSocialException(provider:String, errorCode:String, errorMessage:String, cause: Throwable? = null) :
    ErrorCodedStatusException(errorCode, "$errorMessage (provider: $provider)", cause) {
}
