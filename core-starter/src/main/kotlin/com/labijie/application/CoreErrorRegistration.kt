package com.labijie.application

import com.labijie.application.open.OpenApiErrors

/**
 *
 * @Author: Anders Xiao
 * @Date: 2022/2/17
 * @Description:
 */
class CoreErrorRegistration : IErrorRegistration {
    override fun register(registry: IErrorRegistry) {
        registry.registerErrors(OpenApiErrors)
    }
}