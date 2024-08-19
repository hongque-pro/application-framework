/**
 * @author Anders Xiao
 * @date 2024-08-19
 */
package com.labijie.application.annotation

import com.labijie.application.IErrorRegistration
import com.labijie.application.IErrorRegistry
import com.labijie.application.service.ILocalizationService


internal class ImportedErrorRegistration: IErrorRegistration  {
    lateinit var errorObject: Any

    override fun register(registry: IErrorRegistry, localizationService: ILocalizationService) {
        if(this::errorObject.isInitialized) {
            registry.registerErrors(errorObject, localizationService)
        }
    }
}