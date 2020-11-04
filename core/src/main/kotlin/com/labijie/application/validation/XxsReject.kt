package com.labijie.application.validation

import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-11-13
 */
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [XxsValidator::class])
annotation class XxsReject(
    val message: String = "Xxs content detected",
    val policy: XxsDefinePolicy = XxsDefinePolicy.Strict,
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)