package com.labijie.application.dapr.configuration

import com.labijie.application.dapr.PubsubSide
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * @author Anders Xiao
 * @date 2023-12-04
 */
@ConfigurationProperties("application.dapr.message-service")
class DaprMessageServiceProperties {
    var smsTopic: String = "sms"
    var smsPubsubName: String = "smspubsub"
}