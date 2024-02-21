package com.labijie.application.configuration

import com.labijie.application.doc.DocPropertyCustomizer
import com.labijie.application.doc.DocUtils
import com.labijie.infra.getApplicationName
import com.labijie.infra.oauth2.TwoFactorPrincipal
import com.labijie.infra.utils.nowString
import com.labijie.infra.utils.toLocalDateTime
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.apache.commons.text.CaseUtils
import org.springdoc.core.configuration.SpringDocSecurityOAuth2Customizer
import org.springdoc.core.models.GroupedOpenApi
import org.springdoc.core.utils.SpringDocUtils
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureOrder
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.info.GitProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile
import org.springframework.core.Ordered
import org.springframework.core.env.Environment
import java.time.format.DateTimeFormatter


/**
 *
 * @Author: Anders Xiao
 * @Date: 2021/12/14
 * @Description:
 */
@Configuration(proxyBeanMethods = false)
@ComponentScan(basePackageClasses = [DocPropertyCustomizer::class])
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
class SpringDocAutoConfiguration(private val environment: Environment): InitializingBean, ApplicationContextAware {

    private lateinit var applicationContext: ApplicationContext

    @Autowired(required = false)
    private var gitProperties: GitProperties? = null

    @Bean
    @ConditionalOnMissingBean(OpenAPI::class)
    fun defaultOpenAPI(): OpenAPI {
        return DocUtils.createDefaultOpenAPI(environment.getApplicationName(), gitProperties)
    }


    @Bean
    @Profile("!prod", "!production")
    fun applicationApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("Application")
            .packagesToExclude(
                "com.labijie.infra",
                "org.springframework",
                "io.dapr",
            )
            .pathsToExclude("/oauth2/**")
            .build()
    }


    @Bean
    @ConditionalOnClass(name = ["com.labijie.application.dapr.configuration.ApplicationDaprAutoConfiguration"])
    @Profile("!prod", "!production")
    fun daprApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("Dapr")
            .packagesToScan("io.dapr")
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
    @Profile("!prod", "!production")
    fun oauth2Api(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("OAuth2 Server")
            .pathsToMatch("/oauth2/**")
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