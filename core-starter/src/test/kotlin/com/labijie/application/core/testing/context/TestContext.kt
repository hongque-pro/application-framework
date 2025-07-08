package com.labijie.application.core.testing.context

import com.labijie.application.configuration.ApplicationCoreAutoConfiguration
import com.labijie.application.configuration.OkHttpAutoConfiguration
import com.labijie.application.configuration.RestClientAutoConfiguration
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
    RestClientAutoConfiguration::class,
    com.labijie.application.configuration.RestTemplateAutoConfiguration::class,
    RestTemplateAutoConfiguration::class,
    ApplicationCoreAutoConfiguration::class)
class TestContext {
}