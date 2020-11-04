package com.labijie.application.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-10
 */
@ConfigurationProperties("application.okhttp")
class OkHttpClientProperties {
    var maxConnections:Int = 200
    var sslValidationDisabled:Boolean = true
    var timeToLive:Duration = Duration.ofMinutes(5)
    var connectTimeout:Duration = Duration.ofSeconds(2)
    var readTimeout:Duration = Duration.ofSeconds(5)
    var writeTimeout:Duration = Duration.ofSeconds(5)
    var followRedirects:Boolean = true
}