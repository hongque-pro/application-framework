package com.labijie.application.configuration

import com.labijie.application.ApplicationErrors
import com.labijie.application.IErrorRegistration
import com.labijie.application.IErrorRegistry
import com.labijie.application.open.OpenApiErrors
import com.labijie.application.service.ILocalizationService
import org.springframework.context.MessageSource

/**
 * @author Anders Xiao
 * @date 2023-12-06
 */
class ApplicationErrorsRegistration : IErrorRegistration {
    override fun register(registry: IErrorRegistry, localizationService: ILocalizationService) {
        registry.registerErrors(ApplicationErrors, localizationService)
    }
}