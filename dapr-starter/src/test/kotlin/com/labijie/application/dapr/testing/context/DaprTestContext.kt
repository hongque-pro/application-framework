package com.labijie.application.dapr.testing.context

import com.labijie.application.dapr.configuration.ApplicationDaprAutoConfiguration
import com.labijie.caching.configuration.CachingAutoConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.ImportAutoConfiguration

/**
 * @author Anders Xiao
 * @date 2023-12-10
 */

@ImportAutoConfiguration(CachingAutoConfiguration::class, ApplicationDaprAutoConfiguration::class)
class DaprTestContext {
}