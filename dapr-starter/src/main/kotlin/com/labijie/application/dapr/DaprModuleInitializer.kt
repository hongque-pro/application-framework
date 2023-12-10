package com.labijie.application.dapr

import com.labijie.application.IModuleInitializer
import com.labijie.application.dapr.configuration.DaprProperties
import io.dapr.client.DaprClient

/**
 * @author Anders Xiao
 * @date 2023-12-09
 */
class DaprModuleInitializer : IModuleInitializer {
    override fun getModuleName(): String {
        return "Dapr"
    }

    fun initialize(client: DaprClient, daprProperties: DaprProperties) {

    }
}