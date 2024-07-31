package com.labijie.application.configuration

import com.labijie.application.JsonMode
import com.labijie.application.component.IHumanChecker
import com.labijie.application.component.WebBootPrinter
import com.labijie.application.component.impl.NoneHumanChecker
import com.labijie.application.service.CaptchaHumanChecker
import com.labijie.application.web.WrappedResponseBodyAdvice
import com.labijie.application.web.antMatchers
import com.labijie.application.web.controller.CaptchaController
import com.labijie.application.web.converter.EnhanceStringToEnumConverterFactory
import com.labijie.application.web.handler.ControllerExceptionHandler
import com.labijie.application.web.interceptor.HttpCacheInterceptor
import com.labijie.application.web.interceptor.HumanVerifyInterceptor
import com.labijie.application.web.interceptor.PrincipalArgumentResolver
import com.labijie.infra.isProduction
import com.labijie.infra.json.JacksonHelper
import com.labijie.infra.oauth2.resource.IResourceAuthorizationConfigurer
import jakarta.annotation.security.PermitAll
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.hibernate.validator.HibernateValidator
import org.hibernate.validator.HibernateValidatorConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.env.Environment
import org.springframework.format.FormatterRegistry
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping


/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-05
 */
//@EnableWebMvc
@Configuration(proxyBeanMethods = false)
@Import(DefaultResourceSecurityConfiguration::class, SpringDocAutoConfiguration::class)
@AutoConfigureAfter(Environment::class)
@AutoConfigureBefore(DefaultsAutoConfiguration::class)
@EnableConfigurationProperties(ApplicationWebProperties::class)
@ConditionalOnWebApplication
class ApplicationWebAutoConfiguration(private val properties: ApplicationWebProperties) : WebMvcConfigurer,
    IResourceAuthorizationConfigurer, ApplicationContextAware {

    @Autowired(required = false)
    private var humanChecker: IHumanChecker? = null

    @Autowired
    private lateinit var environment: Environment

    private lateinit var applicationContext: ApplicationContext

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("*")
            .maxAge(3600)
            .allowedHeaders("*")
    }


    override fun addFormatters(registry: FormatterRegistry) {
        registry.addConverterFactory(EnhanceStringToEnumConverterFactory())
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(PrincipalArgumentResolver())
    }

    override fun extendMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        val index = converters.indexOfFirst {
            it is MappingJackson2HttpMessageConverter
        }
        val mapper =
            if (properties.jsonMode == JsonMode.JAVASCRIPT) JacksonHelper.webCompatibilityMapper else JacksonHelper.defaultObjectMapper
        if (index >= 0) {
            converters.removeAt(index)
            converters.add(index, MappingJackson2HttpMessageConverter(mapper))
        } else {
            converters.add(converters.size, MappingJackson2HttpMessageConverter(mapper))
            super.configureMessageConverters(converters)
        }
    }


    override fun configureContentNegotiation(configurer: ContentNegotiationConfigurer) {
        super.configureContentNegotiation(configurer)
        configurer.defaultContentType(MediaType.APPLICATION_JSON)
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
//        if(properties.localeResolver.enabled) {
//            registry.addInterceptor(ApplicationLocaleInterceptor(applicationContext)).order(Ordered.HIGHEST_PRECEDENCE)
//        }
        registry.addInterceptor(
            HumanVerifyInterceptor(
                humanChecker ?: NoneHumanChecker()
            )
        )
        registry.addInterceptor(HttpCacheInterceptor)
    }

    override fun addViewControllers(registry: ViewControllerRegistry) {
        super.addViewControllers(registry)
        if (!environment.isProduction) {
            registry.addRedirectViewController("/swagger", "/swagger-ui.html")
        }
    }

    @Bean
    fun webBootPrinter(): WebBootPrinter {
        return WebBootPrinter()
    }


    @Configuration(proxyBeanMethods = false)
    class HibernateValidationAutoConfiguration {

        @Bean
        @ConditionalOnMissingBean(Validator::class)
        fun validator(): Validator {
            val validatorFactory = Validation.byProvider(HibernateValidator::class.java)
                .configure()
                .addProperty(HibernateValidatorConfiguration.FAIL_FAST, "true")
                .buildValidatorFactory()
            return validatorFactory.validator
        }
    }


    @Bean
    @ConditionalOnMissingBean(MethodValidationPostProcessor::class)
    fun methodValidationPostProcessor(validator: Validator): MethodValidationPostProcessor {
        val postProcessor = MethodValidationPostProcessor()
        postProcessor.setValidator(validator);
        return postProcessor
    }

    @Bean
    fun controllerExceptionHandler(messageSource: MessageSource): ControllerExceptionHandler {
        return ControllerExceptionHandler(messageSource)
    }


    @Bean
    fun wrappedResponseBodyAdvice() = WrappedResponseBodyAdvice()

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnMissingBean(IHumanChecker::class)
    class CaptchaAutoConfiguration {
        @Bean
        fun captchaHumanChecker(applicationProperties: ApplicationCoreProperties): CaptchaHumanChecker {
            return CaptchaHumanChecker(applicationProperties)
        }

        @Bean
        @ConditionalOnProperty(
            prefix = "application.web.service-controllers",
            name = ["enabled"],
            havingValue = "true",
            matchIfMissing = true
        )
        fun captchaController(
            applicationProperties: ApplicationCoreProperties,
            captchaHumanChecker: CaptchaHumanChecker
        ): CaptchaController {
            return CaptchaController(applicationProperties, captchaHumanChecker)
        }

    }

    private fun getPermitAllUrlsFromAnnotations(): Map<HttpMethod, MutableSet<String>> {
        val sets = mutableMapOf<HttpMethod, MutableSet<String>>()
        val requestMappingHandlerMapping = applicationContext.getBean(RequestMappingHandlerMapping::class.java)
        val handlerMethodMap = requestMappingHandlerMapping.handlerMethods
        handlerMethodMap.forEach { (key, value) ->
            val anno = value.getMethodAnnotation(PermitAll::class.java) ?: value.beanType.getAnnotation(PermitAll::class.java)
            if (anno != null) {
                key.pathPatternsCondition?.patterns?.let { urls ->
                    key.methodsCondition.methods.forEach { method ->
                        val httpMethod = when (method) {
                            RequestMethod.GET -> HttpMethod.GET
                            RequestMethod.POST -> HttpMethod.POST
                            RequestMethod.PUT -> HttpMethod.PUT
                            RequestMethod.DELETE -> HttpMethod.DELETE
                            RequestMethod.PATCH -> HttpMethod.PATCH
                            RequestMethod.HEAD -> HttpMethod.HEAD
                            RequestMethod.OPTIONS -> HttpMethod.OPTIONS
                            RequestMethod.TRACE -> HttpMethod.TRACE
                            else -> null
                        }
                        httpMethod?.let {
                            val urlList = sets.getOrPut(it) { mutableSetOf() }
                            urls.forEach { u -> urlList.add(u.patternString) }
                        }
                    }
                }
            }
        }
        return sets
    }

    override fun configure(registry: AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry) {
        val list = getPermitAllUrlsFromAnnotations()
        list.forEach { (method, urls) ->
            registry.antMatchers(*urls.toTypedArray(), ignoreCase = true, method = method).permitAll()
        }
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }
}