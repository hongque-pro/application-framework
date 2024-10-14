/**
 * @author Anders Xiao
 * @date 2024-07-24
 */
package com.labijie.application.dapr.components

import com.labijie.application.dapr.DaprClusterEvent
import org.springframework.core.Ordered


interface IClusterEventListener : Ordered {
    override fun getOrder() = 0
    fun onEvent(event: DaprClusterEvent)
}