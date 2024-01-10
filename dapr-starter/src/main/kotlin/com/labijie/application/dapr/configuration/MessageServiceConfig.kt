package com.labijie.application.dapr.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * @author Anders Xiao
 * @date 2023-12-04
 */
class MessageServiceConfig {
    var smsTopic: String = "sms"
    var smsPubsubName: String = "sms_pubsub"
}