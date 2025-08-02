package com.labijie.application

import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
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

    fun <T: Any> validate(bean: T, throwsIfError: Boolean = true): Set<ConstraintViolation<T>> {
        val result = validator.validate(bean)

        if (!result.isEmpty() && throwsIfError) {
            // 创建包含所有错误的异常
            throw ConstraintViolationException("Bean validation failed", result)
        }

        return result
    }
}