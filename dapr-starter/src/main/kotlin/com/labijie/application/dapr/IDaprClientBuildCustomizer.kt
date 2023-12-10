package com.labijie.application.dapr

import io.dapr.client.DaprClientBuilder

/**
 * @author Anders Xiao
 * @date 2023-12-09
 */
interface IDaprClientBuildCustomizer {
    fun customize(build: DaprClientBuilder)
}