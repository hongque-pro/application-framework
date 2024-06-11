/**
 * @author Anders Xiao
 * @date 2024-06-10
 */
package com.labijie.application.validation

import com.labijie.application.ApplicationErrors
import com.labijie.application.SpringContext
import com.labijie.application.component.IStrongPasswordValidator
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext


class StrongPasswordAnnotationValidator : ConstraintValidator<StrongPassword, String> {
    companion object {
        const val DEFAULT_MESSAGE_TEMPLATE = "{app.err.${ApplicationErrors.StrongPasswordConstraintViolation}}"
    }

    private lateinit var messageTemplate: String

    private val validator by lazy {
        SpringContext.current.getBean(IStrongPasswordValidator::class.java)
    }


    override fun initialize(constraintAnnotation: StrongPassword) {
        super.initialize(constraintAnnotation)

        val message = constraintAnnotation.message
        val format =
            if (message.isBlank()) {
                DEFAULT_MESSAGE_TEMPLATE
            } else {
                constraintAnnotation.message
            }
        messageTemplate = ValidationUtils.localeMessage(format, "Strong password constraint violation.")
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        return if (validator.validate(value ?: "", false)) {
            true
        } else {
            context?.disableDefaultConstraintViolation()
            context?.buildConstraintViolationWithTemplate(messageTemplate)?.addConstraintViolation()
            false
        }
    }
}