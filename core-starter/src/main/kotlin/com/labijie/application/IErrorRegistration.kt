package com.labijie.application

import org.springframework.context.MessageSource

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-14
 */
interface IErrorRegistration {
    fun register(registry: IErrorRegistry, messageSource: MessageSource)
}