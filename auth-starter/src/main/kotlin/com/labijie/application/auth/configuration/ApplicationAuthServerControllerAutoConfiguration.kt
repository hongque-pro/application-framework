package com.labijie.application.auth.configuration

import com.labijie.application.auth.controller.AccountSecurityController
import com.labijie.application.auth.controller.RegisterController
import com.labijie.infra.oauth2.configuration.OAuth2ServerAutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Import

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-12
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(OAuth2ServerAutoConfiguration::class)
class ApplicationAuthServerControllerAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(
        name = ["application.auth.register-controller-enabled"],
        havingValue = "true",
        matchIfMissing = true
    )
    @Import(RegisterController::class)
    protected class RegisterControllerImport


    @Configuration(proxyBeanMethods = false)
    @ComponentScan(
        basePackageClasses = [AccountSecurityController::class],
        excludeFilters = [ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,classes = [RegisterController::class])]
    )
    protected class AuthControllerImport

}