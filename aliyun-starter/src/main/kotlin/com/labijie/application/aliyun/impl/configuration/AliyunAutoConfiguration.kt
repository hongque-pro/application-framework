package com.labijie.application.aliyun.impl.configuration

import com.labijie.application.aliyun.annotation.EnableAliyunServices
import com.labijie.application.aliyun.impl.AliyunModuleInitializer
import com.labijie.application.aliyun.impl.component.AfsHumanChecker
import com.labijie.application.configuration.SmsTemplates
import com.labijie.infra.configuration.CommonsAutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-22
 */
@Configuration(proxyBeanMethods = false)
@EnableAliyunServices
@AutoConfigureBefore(CommonsAutoConfiguration::class)
@ComponentScan(basePackageClasses = [AfsHumanChecker::class])
class AliyunAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(AliyunModuleInitializer::class)
    fun aliyunModuleInitializer(): AliyunModuleInitializer {
        return AliyunModuleInitializer()
    }

    @Bean
    @ConfigurationProperties("aliyun.sms.templates")
    fun smsTemplates(): SmsTemplates = SmsTemplates()
}