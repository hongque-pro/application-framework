/**
 * @author Anders Xiao
 * @date 2024-10-12
 */
package com.labijie.application.auth.configuration

import com.labijie.application.auth.controller.PhoneBasedAccountController
import org.springframework.context.annotation.ImportSelector
import org.springframework.core.type.AnnotationMetadata


class PhoneBasedAccountEndpointImportSelector : ImportSelector {
    override fun selectImports(importingClassMetadata: AnnotationMetadata): Array<String> {
        //importingClassMetadata.getAnnotationAttributes(EnablePhoneBasedAccountEndpoint::class.java.name)
        return arrayOf(PhoneBasedAccountController::class.java.name)
    }
}