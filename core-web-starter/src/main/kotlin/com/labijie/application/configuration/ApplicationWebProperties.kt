/**
 * @author Anders Xiao
 * @date 2024-02-21
 */
package com.labijie.application.configuration

import com.labijie.application.JsonMode
import com.labijie.application.UrlProtocol
import com.labijie.application.component.impl.NoneHumanChecker
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty


@ConfigurationProperties("application.web")
data class ApplicationWebProperties(
    /**
     * 当配置为 JsonMode.JAVASCRIPT 时，整个网站的 json 响应将兼容 javascript (long 等类型自动变为 string).
     */
    var jsonMode: JsonMode = JsonMode.JAVASCRIPT,

    val serverBaseUrl: String = "",

    @NestedConfigurationProperty
    val localeResolver: LocaleResolverSettings = LocaleResolverSettings()
)