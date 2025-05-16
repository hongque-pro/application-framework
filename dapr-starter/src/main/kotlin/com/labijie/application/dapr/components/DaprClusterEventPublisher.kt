/**
 * @author Anders Xiao
 * @date 2024-07-24
 */
package com.labijie.application.dapr.components

import com.labijie.application.dapr.DaprClusterEvent
import com.labijie.application.dapr.configuration.DaprProperties
import com.labijie.infra.utils.throwIfNecessary
import io.dapr.client.DaprClient
import io.dapr.exceptions.DaprException
import org.slf4j.LoggerFactory
import java.time.Duration


class DaprClusterEventPublisher(
    private val properties: DaprProperties,
    private val daprClient: DaprClient) : IClusterEventPublisher {

    companion object {
        private val logger by lazy { LoggerFactory.getLogger(DaprClusterEventPublisher::class.java) }
    }
    private fun publishEvent(daprClusterEvent: DaprClusterEvent, ttl: Duration, throwIfError: Boolean) {
        val settings = properties.clusterNotification
        //Using Dapr SDK to invoke output binding
        try {
            daprClient.publishEvent(settings.pubsub, settings.topic, daprClusterEvent, ).block()
        }
        catch (e: DaprException) {
            if(e.errorCode == "UNAVAILABLE") {
                logger.error(
                    "[DaprClusterEvent] Call dapr publish (pubsub: ${settings.pubsub}, topic: ${settings.topic}) failed, server unavailable."
                )
            }else {
                logger.error(
                    "[DaprClusterEvent] Call dapr publish (pubsub: ${settings.pubsub}, topic: ${settings.topic}) failed .",
                    e
                )
            }
            e.throwIfNecessary()
            if(throwIfError) {
                throw e
            }
        }
        catch (e: Throwable) {
            logger.error("[DaprClusterEvent] Call dapr publish (pubsub: ${settings.pubsub}, topic: ${settings.topic}) failed .", e)
            e.throwIfNecessary()
            if(throwIfError) {
                throw e
            }
        }
    }

    override fun publishEvent(eventName: String, args: String?, ttl: Duration, throwIfError: Boolean) {
        DaprClusterEvent(eventName, IClusterEventPublisher.PublisherId, args).let {
            this.publishEvent(it, ttl, throwIfError)
        }
    }

}