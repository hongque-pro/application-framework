/**
 * @author Anders Xiao
 * @date 2024-07-24
 */
package com.labijie.application.dapr.configuration


data class ClusterNotificationSettings(
    var subscribeEvent: Boolean = false,
    var pubsub: String = "pubsub",
    var topic : String = "cluster-event"
)