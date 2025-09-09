package com.labijie.application.core.testing.context

import com.labijie.application.configuration.ApplicationCoreAutoConfiguration
import com.labijie.application.configuration.OkHttpAutoConfiguration
import com.labijie.application.configuration.RestClientCustomizationAutoConfiguration
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration
import org.springframework.context.annotation.Configuration

/**
 * @author Anders Xiao
 * @date 2023-12-02
 */
@Configuration
@ImportAutoConfiguration(
    OkHttpAutoConfiguration::class,
    RestClientCustomizationAutoConfiguration::class,
    com.labijie.application.configuration.RestTemplateCustomizationAutoConfiguration::class,
    RestTemplateAutoConfiguration::class,
    ApplicationCoreAutoConfiguration::class)
class TestContext {
}