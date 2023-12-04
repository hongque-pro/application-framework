package com.labijie.application.dapr.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * @author Anders Xiao
 * @date 2023-12-04
 */
@ConfigurationProperties("application.dapr")
class DaprProperties {
    var pubServiceEnabled: Boolean = false
    var subServiceEnabled: Boolean = false
}