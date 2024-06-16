/**
 * @author Anders Xiao
 * @date 2024-06-16
 */
package com.labijie.application.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass


@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [DisplayNameAnnotationValidator::class])
annotation class DisplayName(
    val message: String = DisplayNameAnnotationValidator.DEFAULT_MESSAGE_TEMPLATE,
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)