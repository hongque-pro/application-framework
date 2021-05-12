package com.labijie.application.excel.configuration

import com.labijie.application.configuration.DefaultsAutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration


/**
 *
 * @author lishiwen
 * @date 19-9-26
 * @since JDK1.8
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(DefaultsAutoConfiguration::class)
@ComponentScan("com.labijie.application.excel.component")
class PoiAutoConfiguration
