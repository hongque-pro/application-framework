package com.labijie.application

import com.labijie.application.open.OpenApiErrors
import com.labijie.application.service.ILocalizationService
import org.springframework.context.MessageSource

/**
 *
 * @Author: Anders Xiao
 * @Date: 2022/2/17
 * @Description:
 */
class CoreErrorRegistration : IErrorRegistration {
    override fun register(registry: IErrorRegistry, localizationService: ILocalizationService) {
        registry.registerErrors(OpenApiErrors, localizationService)
    }
}