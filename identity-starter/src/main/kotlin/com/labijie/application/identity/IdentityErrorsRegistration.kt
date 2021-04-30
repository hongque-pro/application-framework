package com.labijie.application.identity

import com.labijie.application.IErrorRegistration
import com.labijie.application.IErrorRegistry

/**
 *
 * @Auther: AndersXiao
 * @Date: 2021-04-29 12:31
 * @Description:
 */
class IdentityErrorsRegistration  : IErrorRegistration {
    override fun register(registry: IErrorRegistry) {
        registry.registerErrors(IdentityErrors)
    }
}