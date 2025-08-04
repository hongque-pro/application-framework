package com.labijie.application.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

/**
 * @author Anders Xiao
 * @date 2025/8/4
 */
@Target(
    AnnotationTarget.CLASS
)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [PhoneNumberAnnotationValidator::class])
annotation class PhoneNumber(
    val countryCodeField: String,
    val numberField: String,
    val message: String = PhoneNumberAnnotationValidator.DEFAULT_MESSAGE_TEMPLATE,
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)