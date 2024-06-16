/**
 * @author Anders Xiao
 * @date 2024-06-12
 */
package com.labijie.application.auth.model


data class OAuth2ClientEntry(val provider: String, val name: String, val authorizeUri: String)