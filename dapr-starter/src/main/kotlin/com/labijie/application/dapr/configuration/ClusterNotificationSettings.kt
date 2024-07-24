/**
 * @author Anders Xiao
 * @date 2024-07-24
 */
package com.labijie.application.dapr.configuration

import org.springframework.boot.context.properties.NestedConfigurationProperty


data class ClusterNotificationSettings(

    var enabled: Boolean = false,

    @NestedConfigurationProperty
    val binding: BindingConfig = BindingConfig(bindingName = "cluster-notification")
)