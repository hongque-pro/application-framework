/**
 * @author Anders Xiao
 * @date 2024-10-12
 */
package com.labijie.application.auth.annotation

import com.labijie.application.auth.configuration.PhoneBasedAccountEndpointImportSelector
import org.springframework.context.annotation.Import


@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Import(PhoneBasedAccountEndpointImportSelector::class)
annotation class EnablePhoneBasedAccountEndpoint