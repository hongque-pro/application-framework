/**
 * @author Anders Xiao
 * @date 2024-07-24
 */
package com.labijie.application.dapr.components

import com.labijie.application.dapr.DaprClusterEvent
import org.springframework.core.Ordered


interface IDaprClusterEventListener : Ordered {
    override fun getOrder() = 0

    val forEvents: Set<String>
    fun onEvent(event: DaprClusterEvent)
}