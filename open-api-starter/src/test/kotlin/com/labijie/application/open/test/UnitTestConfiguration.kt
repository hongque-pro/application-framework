package com.labijie.application.open.test

import com.labijie.application.identity.IdentityUtils
import com.labijie.application.identity.service.IUserService
import com.labijie.application.open.configuration.OpenApiAutoConfiguration
import com.labijie.infra.impl.DebugIdGenerator
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration(OpenApiAutoConfiguration::class)
class UnitTestConfiguration {

    @Bean
    fun debugIdGeneratorService() : DebugIdGenerator {
        return DebugIdGenerator()
    }

    @Bean
    fun mockUserService() : IUserService {
        val us =  mock(IUserService::class.java)
        val u = IdentityUtils.createUser(OpenAppServiceTester.TestUserId, "test", "13888888888", 0)
        Mockito.`when`(us.getUserById(OpenAppServiceTester.TestUserId)).thenReturn(u)
        return us
    }
}