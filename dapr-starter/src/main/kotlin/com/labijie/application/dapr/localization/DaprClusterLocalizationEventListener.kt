/**
 * @author Anders Xiao
 * @date 2024-07-24
 */
package com.labijie.application.dapr.localization

import com.labijie.application.dapr.BuiltInClusterEvents
import com.labijie.application.dapr.DaprClusterEvent
import com.labijie.application.dapr.components.IDaprClusterEventListener
import com.labijie.application.service.ILocalizationService
import org.springframework.beans.factory.annotation.Autowired


class DaprClusterLocalizationEventListener(
    @param: Autowired
    private val localizationService: ILocalizationService) :
    IDaprClusterEventListener {

    override val forEvents: Set<String>
        get() = setOf(BuiltInClusterEvents.LOCALIZATION_CHANGED)

    override fun onEvent(event: DaprClusterEvent) {
        localizationService.reload()
    }
}