package com.labijie.application.open.configuration

import com.labijie.application.auth.service.IUserService
import com.labijie.application.open.component.ApiSignatureMvcInterceptor
import com.labijie.application.open.component.OpenApiFilter
import com.labijie.application.open.data.mapper.OpenAppMapper
import com.labijie.application.open.data.mapper.OpenPartnerMapper
import com.labijie.application.open.data.mapper.OpenPartnerUserMapper
import com.labijie.application.open.service.IOpenAppService
import com.labijie.application.open.service.IOpenPartnerService
import com.labijie.application.open.service.impl.OpenAppService
import com.labijie.application.open.service.impl.OpenPartnerService
import com.labijie.infra.IIdGenerator
import com.labijie.infra.oauth2.resource.IResourceAuthorizationConfigurer
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.CorsFilter
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import javax.servlet.Filter


@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OpenApiProperties::class)
class OpenApiAutoConfiguration {

    @ConditionalOnMissingBean(IOpenAppService::class)
    @Bean
    fun openAppService(
        idGenerator: IIdGenerator,
        transactionTemplate: TransactionTemplate,
        partnerMapper: OpenPartnerMapper,
        appMapper: OpenAppMapper
    ) : IOpenAppService{
         return OpenAppService(idGenerator, transactionTemplate, partnerMapper, appMapper)
    }

    @ConditionalOnMissingBean(IOpenPartnerService::class)
    @Bean
    fun openPartnerService(
        idGenerator: IIdGenerator,
        transactionTemplate: TransactionTemplate,
        partnerMapper: OpenPartnerMapper,
        partnerUserMapper: OpenPartnerUserMapper,
        userService: IUserService
    ) : IOpenPartnerService{
        return OpenPartnerService(idGenerator, transactionTemplate, partnerMapper, partnerUserMapper, userService)
    }


    @Bean
    @ConditionalOnWebApplication(type= ConditionalOnWebApplication.Type.SERVLET)
    fun apiSignatureMvcInterceptor(appService: IOpenAppService): ApiSignatureMvcInterceptor {
        return ApiSignatureMvcInterceptor(appService)
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication(type= ConditionalOnWebApplication.Type.SERVLET)
    protected class WeMvcInterceptorConfiguration(
        private val apiSignatureMvcInterceptor: ApiSignatureMvcInterceptor,
        private val apiProperties:OpenApiProperties) : WebMvcConfigurer, IResourceAuthorizationConfigurer {

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

        override fun configure(registry: ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry) {
            registry.antMatchers(apiProperties.pathPattern, apiProperties.jsApiCors.pathPattern).permitAll()
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