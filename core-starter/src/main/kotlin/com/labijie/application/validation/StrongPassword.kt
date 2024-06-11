package com.labijie.application.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

/**
 * @author Anders Xiao
 * @date 2024-06-10
 */
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.VALUE_PARAMETER
)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [StrongPasswordAnnotationValidator::class])
annotation class StrongPassword(
    val message: String = UsernameAnnotationValidator.DEFAULT_MESSAGE_TEMPLATE,
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
