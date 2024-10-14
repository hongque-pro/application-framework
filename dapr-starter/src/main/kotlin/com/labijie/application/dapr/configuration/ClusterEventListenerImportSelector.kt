/**
 * @author Anders Xiao
 * @date 2024-10-14
 */
package com.labijie.application.dapr.configuration

import com.labijie.application.dapr.BuiltInClusterEvents
import com.labijie.application.dapr.annotation.EnableDaprClusterEventListener
import com.labijie.application.dapr.controller.ClusterNotificationDaprController
import com.labijie.application.dapr.localization.DaprClusterLocalizationEventListener
import org.springframework.context.annotation.ImportSelector
import org.springframework.core.type.AnnotationMetadata


class ClusterEventListenerImportSelector : ImportSelector {
    companion object {
        private val allowEvents = mutableSetOf("")
        private val excludeEvents = mutableSetOf("")

        fun includeEvent(event: String): Boolean {
            return (allowEvents.contains("*") || allowEvents.contains(event)) && !excludeEvents.contains("*") && !excludeEvents.contains(event)
        }
    }

    override fun selectImports(importingClassMetadata: AnnotationMetadata): Array<String> {
        val events = importingClassMetadata.getAnnotationAttributes(EnableDaprClusterEventListener::class.java.name)
            ?.get(EnableDaprClusterEventListener::events.name)
                as? Array<*> ?: arrayOf<String>()

        val exclude = importingClassMetadata.getAnnotationAttributes(EnableDaprClusterEventListener::class.java.name)
            ?.get(EnableDaprClusterEventListener::excludeEvents.name)
                as? Array<*> ?: arrayOf<String>()

        if (exclude.isNotEmpty()) {
            exclude.forEach {
                if (it is String) {
                    if (!excludeEvents.contains("*")) {
                        excludeEvents.add(it)
                    }
                }
            }
        }

        var count = 0
        if (events.isNotEmpty()) {
            events.forEach {
                if (it is String) {
                    count++
                    if (!allowEvents.contains("*")) {
                        allowEvents.add(it)
                    }
                }
            }
            if (count > 0 && !excludeEvents.contains("*")) {
                val beans = mutableListOf(ClusterNotificationDaprController::class.java.name)
                if(includeEvent(BuiltInClusterEvents.LOCALIZATION_CHANGED)) {
                    beans.add(DaprClusterLocalizationEventListener::class.java.name)
                }
                return beans.toTypedArray()
            }
        }
        return arrayOf()
    }
}