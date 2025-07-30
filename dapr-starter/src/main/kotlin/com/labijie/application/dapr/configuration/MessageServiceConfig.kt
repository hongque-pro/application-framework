package com.labijie.application.dapr.configuration

/**
 * @author Anders Xiao
 * @date 2023-12-04
 */
class MessageServiceConfig {
    var enabled: Boolean = false
    var smsTopic: String = "sms"
    var smsPubsubName: String = "pubsub"
}