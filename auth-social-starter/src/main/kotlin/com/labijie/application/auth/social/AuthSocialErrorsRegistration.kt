package com.labijie.application.auth.social

import com.labijie.application.IErrorRegistration
import com.labijie.application.IErrorRegistry

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-14
 */
class AuthSocialErrorsRegistration : IErrorRegistration {
    override fun register(registry: IErrorRegistry) {
        registry.registerErrors(AuthSocialErrors)
    }
}