package com.labijie.application.configuration

import com.labijie.application.doc.DocPropertyCustomizer
import com.labijie.infra.getApplicationName
import com.labijie.infra.oauth2.TwoFactorPrincipal
import org.springdoc.core.models.GroupedOpenApi
import org.springdoc.core.utils.SpringDocUtils
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.env.Environment


/**
 *
 * @Author: Anders Xiao
 * @Date: 2021/12/14
 * @Description:
 */
@Configuration(proxyBeanMethods = false)
class SpringDocAutoConfiguration(private val environment: Environment): InitializingBean {


    @Bean
    fun enumValuePropertyCustomizer(): DocPropertyCustomizer {
        return DocPropertyCustomizer()
    }

    @Bean
    @Profile("!prod", "!production")
    fun applicationApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group(environment.getApplicationName())
            .packagesToExclude("com.labijie.application", "com.labijie.infra", "org.springframework")
            .pathsToExclude("/oauth2/**")
            .build()
    }


//    @Bean
//    @Profile("!prod", "!production")
//    fun infraApi(): GroupedOpenApi {
//        return GroupedOpenApi.builder()
//            .group("Infra Library")
//            .packagesToScan("com.labijie.infra")
//            .pathsToExclude("/oauth2/**")
//            .build()
//    }

    @Bean
    @Profile("!prod", "!production")
    fun frameworkApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("Application Framework")
            .packagesToScan("com.labijie.application")
            .pathsToExclude("/oauth2/**")
            .build()
    }

    @Bean
    @Profile("!prod", "!production")
    fun oauth2Api(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("Security OAuth2")
            .pathsToMatch("com.labijie.infra.oauth2", "org.springframework.security.oauth2.server")
            .build()
    }

    @Bean
    @Profile("!prod", "!production")
    fun springApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("Spring Framework")
            .packagesToScan("org.springframework")
            .pathsToExclude("/oauth2/**")
            .build()

    }

    private fun Environment.getDocVersion(): String {
        return this.getProperty("springdoc.version") ?: "Release"
    }

    override fun afterPropertiesSet() {
        SpringDocUtils.getConfig().addRequestWrapperToIgnore(TwoFactorPrincipal::class.java)
    }
}