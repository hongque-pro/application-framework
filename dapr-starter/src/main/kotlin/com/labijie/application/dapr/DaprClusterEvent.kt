/**
 * @author Anders Xiao
 * @date 2024-07-24
 */
package com.labijie.application.dapr


data class DaprClusterEvent(
    var eventName: String,
    var publisherId: String,
    var args: String?
)