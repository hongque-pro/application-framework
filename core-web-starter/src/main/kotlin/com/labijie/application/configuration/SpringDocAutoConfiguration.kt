package com.labijie.application.configuration

import com.labijie.application.SpringContext.getApplicationGitProperties
import com.labijie.application.doc.DocPropertyCustomizer
import com.labijie.application.doc.DocServerBaseUrlCustomizer
import com.labijie.application.doc.DocUtils
import com.labijie.infra.getApplicationName
import com.labijie.infra.oauth2.TwoFactorPrincipal
import io.swagger.v3.oas.models.OpenAPI
import org.springdoc.core.configuration.SpringDocSecurityOAuth2Customizer
import org.springdoc.core.models.GroupedOpenApi
import org.springdoc.core.utils.SpringDocUtils
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.autoconfigure.AutoConfigureOrder
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.env.Environment


/**
 *
 * @Author: Anders Xiao
 * @Date: 2021/12/14
 * @Description:
 */
@Configuration(proxyBeanMethods = false)
@ComponentScan(basePackageClasses = [DocPropertyCustomizer::class])
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
@ConditionalOnWebApplication
class SpringDocAutoConfiguration(private val environment: Environment): InitializingBean, ApplicationContextAware {

    private lateinit var applicationContext: ApplicationContext


    @Bean
    fun docServerBaseUrlCustomizer(webProperties: ApplicationWebProperties): DocServerBaseUrlCustomizer {
        return DocServerBaseUrlCustomizer(environment, webProperties)
    }

    @Bean
    @ConditionalOnMissingBean(OpenAPI::class)
    fun defaultOpenAPI(): OpenAPI {
        return DocUtils.createDefaultOpenAPI(environment.getApplicationName(), applicationContext.getApplicationGitProperties())
    }


    @Bean
    fun applicationApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("Application")
            .packagesToExclude(
                "com.labijie.infra",
                "org.springframework",
                "io.dapr",
            )
            .pathsToExclude("/oauth2/**")
            .pathsToExclude("/dapr/**")
            .build()
    }



//    @Bean
//    @ConditionalOnClass(name = ["com.labijie.application.dapr.configuration.ApplicationDaprAutoConfiguration"])
//    @Lazy
//    @Profile("!prod", "!production")
//    fun daprUserApi(): GroupedOpenApi {
//        return GroupedOpenApi.builder()
//            .group("Dapr")
//            .pathsToMatch("/dapr/**")
//            .packagesToScan()
//            .build()
//    }


    @Bean
    @ConditionalOnClass(name=["org.springframework.security.oauth2.server.authorization.OAuth2Authorization"])
    fun oauth2Api(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("OAuth2 Server")
            .pathsToMatch("/oauth2/**")
            .pathsToExclude("/oauth2/unauthorized/**")
            .addOpenApiCustomizer {
                SpringDocSecurityOAuth2Customizer().apply {
                    setApplicationContext(applicationContext)
                }.customise(it)
            }
            .build()
    }


    private fun Environment.getDocVersion(): String {
        return this.getProperty("springdoc.version") ?: "Release"
    }

    override fun afterPropertiesSet() {
        SpringDocUtils.getConfig().addRequestWrapperToIgnore(TwoFactorPrincipal::class.java)
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }
}