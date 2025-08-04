package com.labijie.application.configuration

import com.labijie.application.JsonMode
import com.labijie.application.component.IHumanChecker
import com.labijie.application.service.IOneTimeCodeService
import com.labijie.application.component.WebBootPrinter
import com.labijie.application.doc.DocUtils.isSwaggerEnabled
import com.labijie.application.doc.SpringDocAutoConfiguration
import com.labijie.application.web.antMatchers
import com.labijie.application.web.converter.EnhanceStringToEnumConverterFactory
import com.labijie.application.web.handler.ControllerExceptionHandler
import com.labijie.application.web.interceptor.HttpCacheInterceptor
import com.labijie.application.web.interceptor.HumanVerifyInterceptor
import com.labijie.application.web.interceptor.OneTimeCodeInterceptor
import com.labijie.application.web.interceptor.OneTimeCodeVerifyArgumentResolver
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
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Role
import org.springframework.core.env.Environment
import org.springframework.format.FormatterRegistry
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor
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
@Import(DefaultResourceSecurityConfiguration::class)
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

    @Autowired
    private lateinit var oneTimeCodeService: IOneTimeCodeService


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
        resolvers.add(OneTimeCodeVerifyArgumentResolver())
    }

    override fun extendMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        val index = converters.indexOfFirst {
            it is MappingJackson2HttpMessageConverter
        }
        val mapper =
            if (properties.jsonMode == JsonMode.JAVASCRIPT) JacksonHelper.webCompatibilityMapper else JacksonHelper.defaultObjectMapper
        val converter = MappingJackson2HttpMessageConverter(mapper).apply {
            this.defaultCharset = Charsets.UTF_8
        }
        if (index >= 0) {
            converters.removeAt(index)
            converters.add(index, converter)
        } else {
            converters.add(converters.size, converter)
            super.configureMessageConverters(converters)
        }
    }


    override fun configureContentNegotiation(configurer: ContentNegotiationConfigurer) {
        super.configureContentNegotiation(configurer)
        configurer.defaultContentType(MediaType.APPLICATION_JSON)
    }

    override fun addInterceptors(registry: InterceptorRegistry) {

        humanChecker?.let {
            registry.addInterceptor(
                HumanVerifyInterceptor(
                    it
                )
            )
        }

        registry.addInterceptor(
            OneTimeCodeInterceptor(oneTimeCodeService)
        )
        registry.addInterceptor(HttpCacheInterceptor)
    }

    override fun addViewControllers(registry: ViewControllerRegistry) {
        super.addViewControllers(registry)
        if (environment.isSwaggerEnabled) {
            registry.addRedirectViewController("/swagger", "/swagger-ui/index.html")
        }
    }

    @Bean
    fun webBootPrinter(): WebBootPrinter {
        return WebBootPrinter()
    }


    @Configuration(proxyBeanMethods = false)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
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


    private fun getPermitAllUrlsFromAnnotations(): MutableSet<String> {
        val requestMappingHandlerMapping = applicationContext.getBean(RequestMappingHandlerMapping::class.java)
        val handlerMethodMap = requestMappingHandlerMapping.handlerMethods
        val urlList = mutableSetOf<String>()
        handlerMethodMap.forEach { (key, value) ->
            val anno =
                value.getMethodAnnotation(PermitAll::class.java) ?: value.beanType.getAnnotation(PermitAll::class.java)
            if (anno != null) {
                key.pathPatternsCondition?.patterns?.let { urls ->
                    urls.forEach {
                        urlList.add(it.patternString)
                    }
                }
            }
        }
        return urlList
    }

    override fun configure(registry: AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry) {
        val list = getPermitAllUrlsFromAnnotations()
        registry.antMatchers(*list.toTypedArray(), ignoreCase = true).permitAll()
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

}