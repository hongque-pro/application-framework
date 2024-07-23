/**
 * @author Anders Xiao
 * @date 2024-07-23
 */
package com.labijie.application.dapr

import io.dapr.client.DaprClient
import io.dapr.client.domain.Metadata
import io.dapr.client.domain.PublishEventRequest


fun DaprClient.publishEvent(pubsubName: String, topic: String, data: Any, ttlInSeconds: Int) {
    val metadata = mutableMapOf<String, String>()
    metadata[Metadata.TTL_IN_SECONDS] = ttlInSeconds.toString()

    this.publishEvent(pubsubName, topic, data, metadata).block()
}

fun DaprClient.publishEvent(request: PublishEventRequest, ttlInSeconds: Int) {
    request.metadata[Metadata.TTL_IN_SECONDS] = ttlInSeconds.toString()

    this.publishEvent(request).block()
}