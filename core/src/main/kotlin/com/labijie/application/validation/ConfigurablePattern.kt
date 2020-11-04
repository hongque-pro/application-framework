package com.labijie.application.validation

import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-12
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ConfigurablePatternValidator::class])
annotation class ConfigurablePattern(
    val regexProperty:String,
    val message: String = "{javax.validation.constraints.Pattern.message}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)