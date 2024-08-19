/**
 * @author Anders Xiao
 * @date 2024-07-24
 */
package com.labijie.application.dapr.components

import com.labijie.application.dapr.DaprClusterEvent
import com.labijie.application.dapr.configuration.DaprProperties
import com.labijie.infra.utils.throwIfNecessary
import io.dapr.client.DaprClient
import org.slf4j.LoggerFactory


class DaprClusterEventPublisher(
    private val properties: DaprProperties,
    private val daprClient: DaprClient) : IClusterEventPublisher {

    companion object {
        private val logger by lazy { LoggerFactory.getLogger(DaprClusterEventPublisher::class.java) }
    }
    private fun publishEvent(daprClusterEvent: DaprClusterEvent, throwIfError: Boolean) {
        val bindingConfig = properties.clusterNotification.binding
        //Using Dapr SDK to invoke output binding
        try {
            daprClient.invokeBinding(bindingConfig.bindingName, bindingConfig.operation, daprClusterEvent).block()
        }catch (e: Throwable) {
            logger.error("[DaprClusterEvent] Call dapr output binding (name: ${bindingConfig.bindingName}, operation: ${bindingConfig.operation}) failed .", e)
            e.throwIfNecessary()
            if(throwIfError) {
                throw e
            }
        }
    }

    override fun publishEvent(eventName: String, args: String?, throwIfError: Boolean) {
        DaprClusterEvent(eventName, IClusterEventPublisher.PublisherId, args).let {
            this.publishEvent(it, throwIfError)
        }
    }

}