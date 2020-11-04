package com.labijie.application.excel.configuration

import com.labijie.application.configuration.ApplicationCoreAutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration


/**
 *
 * @author lishiwen
 * @date 19-9-26
 * @since JDK1.8
 */
@Configuration
@AutoConfigureBefore(ApplicationCoreAutoConfiguration::class)
@ComponentScan("com.labijie.application.excel.component")
class PoiAutoConfiguration
