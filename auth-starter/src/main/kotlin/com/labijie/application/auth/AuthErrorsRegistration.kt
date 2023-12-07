package com.labijie.application.auth

import com.labijie.application.IErrorRegistration
import com.labijie.application.IErrorRegistry
import com.labijie.application.service.ILocalizationService
import org.springframework.context.MessageSource

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-14
 */
class AuthErrorsRegistration : IErrorRegistration {
    override fun register(registry: IErrorRegistry, localizationService: ILocalizationService) {
        registry.registerErrors(AuthErrors, localizationService)
    }
}