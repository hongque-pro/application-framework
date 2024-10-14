/**
 * @author Anders Xiao
 * @date 2024-07-24
 */
package com.labijie.application.dapr.components

import java.time.Duration
import java.util.*


interface IClusterEventPublisher {
    companion object {
        val PublisherId = UUID.randomUUID().toString().replace("-", "").lowercase()
    }

    fun publishEvent(eventName: String, args: String? = null, ttl: Duration = Duration.ofSeconds(30), throwIfError: Boolean = false)
}