package com.labijie.application.identity

import com.labijie.application.IErrorRegistration
import com.labijie.application.IErrorRegistry
import com.labijie.application.service.ILocalizationService
import org.springframework.context.MessageSource

/**
 *
 * @Auther: AndersXiao
 * @Date: 2021-04-29 12:31
 * @Description:
 */
class IdentityErrorsRegistration  : IErrorRegistration {
    override fun register(registry: IErrorRegistry, localizationService: ILocalizationService) {
        registry.registerErrors(IdentityErrors, localizationService)
    }
}