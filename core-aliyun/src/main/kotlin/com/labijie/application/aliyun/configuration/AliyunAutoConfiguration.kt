package com.labijie.application.aliyun.configuration

import com.labijie.application.aliyun.AliyunUtils
import com.labijie.infra.spring.configuration.getApplicationName
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-20
 */
@Configuration
@EnableConfigurationProperties(AliyunProperties::class)
class AliyunAutoConfiguration{

    @Bean
    @ConditionalOnMissingBean(AliyunUtils::class)
    fun aliyunUtils(environment: Environment, aliyunConfiguration: AliyunProperties): AliyunUtils {
        return AliyunUtils(environment.getApplicationName(), aliyunConfiguration)
    }
}