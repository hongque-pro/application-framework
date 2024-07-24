/**
 * @author Anders Xiao
 * @date 2024-07-24
 */
package com.labijie.application.dapr.controller

import com.labijie.application.dapr.DaprClusterEvent
import com.labijie.application.dapr.components.IClusterEventListener
import com.labijie.application.dapr.components.IClusterEventPublisher
import com.labijie.infra.utils.throwIfNecessary
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@RestController("/dapr/bindings")
class ClusterNotificationDaprBindingController: ApplicationContextAware {

    companion object {
        private val logger by lazy {
            LoggerFactory.getLogger(ClusterNotificationDaprBindingController::class.java)
        }
    }

    private lateinit var applicationContext: ApplicationContext

    private val listeners by lazy {
        applicationContext.getBeanProvider(IClusterEventListener::class.java).orderedStream().toList()
    }

    @PostMapping("/\${application.dapr.cluster-notification.binding.binding-name:cluster-notification}")
    fun onClusterNotify(@RequestBody(required = true) event: DaprClusterEvent) : Mono<String> {
        return Mono.fromRunnable {
            if(event.publisherId != IClusterEventPublisher.PublisherId) {
                listeners.forEach {
                    if(it.supportEvent(event.eventName)) {
                        try {
                            it.onEvent(event)
                        }catch (e: Throwable) {
                            e.throwIfNecessary()
                            logger.error("An error occurred while listening to cluster event (listener: ${it::class.java.simpleName}).", e)
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