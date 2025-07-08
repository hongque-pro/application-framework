package com.labijie.application

import jakarta.validation.ConstraintViolation
import jakarta.validation.Validation

/**
 * @author Anders Xiao
 * @date 2025/7/4
 */
object BeanValidator {
    private val validator by lazy {
        val factory = Validation.buildDefaultValidatorFactory()
         factory.validator
    }

    fun <T: Any> validate(bean: T): Set<ConstraintViolation<T>> {
        return validator.validate(bean)
    }
}