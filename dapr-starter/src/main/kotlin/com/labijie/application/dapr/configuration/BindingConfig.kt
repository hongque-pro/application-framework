/**
 * @author Anders Xiao
 * @date 2024-07-24
 */
package com.labijie.application.dapr.configuration


data class BindingConfig(
    var bindingName: String = "",
    var operation : String = "create"
)