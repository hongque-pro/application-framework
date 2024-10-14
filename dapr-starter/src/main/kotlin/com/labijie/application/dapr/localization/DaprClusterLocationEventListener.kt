/**
 * @author Anders Xiao
 * @date 2024-07-24
 */
package com.labijie.application.dapr.localization

import com.labijie.application.dapr.DaprClusterEvent
import com.labijie.application.dapr.components.IClusterEventListener
import com.labijie.application.service.ILocalizationService


class DaprClusterLocationEventListener(private val localizationService: ILocalizationService) : IClusterEventListener {

    override fun onEvent(event: DaprClusterEvent) {
        localizationService.reload()
    }
}