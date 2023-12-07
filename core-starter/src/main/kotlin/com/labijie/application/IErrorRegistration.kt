package com.labijie.application

import com.labijie.application.service.ILocalizationService
import org.springframework.context.MessageSource

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-14
 */
interface IErrorRegistration {
    fun register(registry: IErrorRegistry, localizationService: ILocalizationService)
}