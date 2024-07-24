/**
 * @author Anders Xiao
 * @date 2024-07-24
 */
package com.labijie.application.dapr.localization

import com.labijie.application.dapr.BuiltInClusterEvents
import com.labijie.application.dapr.components.IClusterEventPublisher
import com.labijie.application.localization.ILocalizationChangedListener


class DaprLocalLocalizationListener(
    private val clusterEventPublisher: IClusterEventPublisher) : ILocalizationChangedListener {

    override fun onChanged() {
        clusterEventPublisher.publishEvent(BuiltInClusterEvents.LOCALIZATION_CHANGED)
    }
}