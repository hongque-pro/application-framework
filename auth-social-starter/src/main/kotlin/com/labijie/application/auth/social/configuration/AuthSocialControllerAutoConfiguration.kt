package com.labijie.application.auth.social.configuration

import com.labijie.application.auth.configuration.ApplicationAuthServerAutoConfiguration
import com.labijie.application.auth.social.controller.AccountSocialController
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.annotation.Order

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-11
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(ApplicationAuthServerAutoConfiguration::class)
@Import(AccountSocialController::class)
@Order(Int.MAX_VALUE)
class AuthSocialControllerAutoConfiguration