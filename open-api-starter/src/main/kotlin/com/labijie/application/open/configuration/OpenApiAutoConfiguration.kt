package com.labijie.application.open.configuration

import com.labijie.application.identity.service.IUserService
import com.labijie.application.open.component.ApiSignatureMvcInterceptor
import com.labijie.application.open.component.OpenApiFilter
import com.labijie.application.open.data.OpenAppTable
import com.labijie.application.open.service.IOpenAppService
import com.labijie.application.open.service.IOpenPartnerService
import com.labijie.application.open.service.impl.OpenAppService
import com.labijie.application.open.service.impl.OpenPartnerService
import com.labijie.infra.IIdGenerator
import com.labijie.infra.oauth2.resource.IResourceAuthorizationConfigurer
import com.labijie.infra.orm.annotation.TableScan
import com.labijie.infra.utils.logger
import jakarta.servlet.Filter
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.CorsFilter
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OpenApiProperties::class)
@TableScan(basePackageClasses = [OpenAppTable::class])
class OpenApiAutoConfiguration {


    @ConditionalOnMissingBean(IOpenAppService::class)
    @Bean
    fun openAppService(
        idGenerator: IIdGenerator,
        transactionTemplate: TransactionTemplate,
    ): IOpenAppService {
        return OpenAppService(idGenerator, transactionTemplate)
    }

    @ConditionalOnMissingBean(IOpenPartnerService::class)
    @Bean
    fun openPartnerService(
        idGenerator: IIdGenerator,
        transactionTemplate: TransactionTemplate,
        userService: IUserService
    ): IOpenPartnerService {
        return OpenPartnerService(idGenerator, transactionTemplate, userService)
    }


    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    fun apiSignatureMvcInterceptor(appService: IOpenAppService): ApiSignatureMvcInterceptor {
        return ApiSignatureMvcInterceptor(appService)
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    protected class WeMvcInterceptorConfiguration(
        private val apiSignatureMvcInterceptor: ApiSignatureMvcInterceptor,
        private val apiProperties: OpenApiProperties
    ) : WebMvcConfigurer, IResourceAuthorizationConfigurer {

        override fun configure(registry: AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry) {
            if (apiProperties.pathPattern.isNotBlank()) {
                registry.requestMatchers(apiProperties.pathPattern).permitAll()
            } else {
                logger.warn("open api path pattern is blank, none api applied.")
            }
        }

        @Bean
        fun apiCorsFilter(openAppService: IOpenAppService): CorsFilter {
            val corsConfigurationSource = JdbcCorsConfigurationSource(apiProperties, openAppService)
            return CorsFilter(corsConfigurationSource)
        }

        @Bean
        fun apiPathFilterRegistrationBean(): FilterRegistrationBean<out Filter>? {
            val bean = FilterRegistrationBean(OpenApiFilter())
            bean.setName("OpenApiFilter")
            bean.addUrlPatterns(apiProperties.pathPattern)
            bean.order = 100
            return bean
        }

        override fun addInterceptors(registry: InterceptorRegistry) {
            super.addInterceptors(registry)

            val matcher = AntPathMatcher().apply {
                this.setCaseSensitive(false)
            }

            registry.addInterceptor(apiSignatureMvcInterceptor).apply {
                this.pathMatcher(matcher)
                this.addPathPatterns(apiProperties.pathPattern)
            }
        }
    }

}