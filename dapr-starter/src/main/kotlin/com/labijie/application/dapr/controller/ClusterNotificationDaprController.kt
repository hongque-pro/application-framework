/**
 * @author Anders Xiao
 * @date 2024-07-24
 */
package com.labijie.application.dapr.controller

import com.labijie.application.dapr.DaprClusterEvent
import com.labijie.application.dapr.components.IClusterEventPublisher
import com.labijie.application.dapr.components.IDaprClusterEventListener
import com.labijie.application.dapr.configuration.ClusterEventListenerImportSelector
import com.labijie.infra.utils.throwIfNecessary
import io.dapr.Topic
import io.dapr.client.domain.CloudEvent
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@RestController
@RequestMapping("/dapr/sub")
class ClusterNotificationDaprController: ApplicationContextAware {

    companion object {
        private val logger by lazy {
            LoggerFactory.getLogger(ClusterNotificationDaprController::class.java)
        }
    }

    private lateinit var applicationContext: ApplicationContext

    private val listeners by lazy {
        applicationContext.getBeanProvider(IDaprClusterEventListener::class.java).orderedStream().toList()
    }

    @Topic(
        name = "\${application.dapr.cluster-notification.topic:cluster-event}",
        pubsubName = "\${application.dapr.cluster-notification.pubsub:pubsub}"
    )
    @PostMapping("/cluster-event")
    fun onClusterNotify(@RequestBody(required = true) cloudEvent: CloudEvent<DaprClusterEvent>) : Mono<String> {
        return Mono.fromRunnable {
            val data = cloudEvent.data
            if(data.publisherId != IClusterEventPublisher.PublisherId) {
                listeners.forEach {
                    if(ClusterEventListenerImportSelector.includeEvent(data.eventName)) {
                        try {
                            if(it.forEvents.contains(data.eventName)) {
                                it.onEvent(data)
                            }
                        }catch (e: Throwable) {
                            e.throwIfNecessary()
                            logger.error("An error occurred while listening to cluster event (listener: ${it::class.java.simpleName}, event: ${data.eventName}).", e)
                        }
                    }
                }
            }
        }
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }
}