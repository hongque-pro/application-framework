package com.labijie.application.configuration

import com.labijie.application.ApplicationErrors
import com.labijie.application.IErrorRegistration
import com.labijie.application.IErrorRegistry
import org.springframework.context.MessageSource

/**
 * @author Anders Xiao
 * @date 2023-12-06
 */
class ApplicationErrorsRegistration : IErrorRegistration {
    override fun register(registry: IErrorRegistry, messageSource: MessageSource) {
        registry.registerErrors(ApplicationErrors, messageSource)
    }
}