/**
 * @author Anders Xiao
 * @date 2024-07-05
 */
package com.labijie.application.auth.configuration


data class OAuth2LoginSettings(
    var redirectionBaseUrl: String = "",
    var handlerPageUri: String = "/"
)