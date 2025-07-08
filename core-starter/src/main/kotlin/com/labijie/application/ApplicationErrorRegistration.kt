package com.labijie.application

import com.labijie.application.service.ILocalizationService

/**
 * @author Anders Xiao
 * @date 2024-08-19
 */
internal class ApplicationErrorRegistration  {
    var errorClassName: String? = null

    fun register(registry: IErrorRegistry, localizationService: ILocalizationService) {

        errorClassName?.let {
            className->
            Class.forName(className).kotlin.objectInstance?.let {
                registry.registerErrors(it, localizationService)
            }
        }
    }
}