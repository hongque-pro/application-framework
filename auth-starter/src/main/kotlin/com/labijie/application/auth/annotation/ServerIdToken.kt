package com.labijie.application.auth.annotation
import com.labijie.infra.oauth2.StandardOidcUser

/**
 * @author Anders Xiao
 * @date 2025/8/1
 */

/**
 * Get `id-token` or decoded oauth2 user info.
 *
 * Can accept parameters of type [StandardOidcUser] or [String] in controller methods.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class ServerIdToken(val required: Boolean = true, val ignoreExpiration: Boolean = false)
