/**
 * @author Anders Xiao
 * @date 2024-06-16
 */
package com.labijie.application.validation

import com.labijie.application.ApplicationErrors
import com.labijie.application.SpringContext
import com.labijie.application.component.IDisplayNameValidator
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext


class DisplayNameAnnotationValidator : ConstraintValidator<DisplayName, String> {
    companion object {
        const val DEFAULT_MESSAGE_TEMPLATE = "{app.err.${ApplicationErrors.InvalidDisplayName}}"
    }

    private lateinit var messageTemplate: String

    private val validator by lazy {
        SpringContext.current.getBean(IDisplayNameValidator::class.java)
    }


    override fun initialize(constraintAnnotation: DisplayName) {
        super.initialize(constraintAnnotation)

        val message = constraintAnnotation.message
        val format =
            if (message.isBlank()) {
                DEFAULT_MESSAGE_TEMPLATE
            } else {
                constraintAnnotation.message
            }
        messageTemplate = ValidationUtils.localeMessage(format,"Invalid display name.")
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        return if(validator.validate(value ?: "", false)) {
            true
        }else {
            context?.disableDefaultConstraintViolation()
            context?.buildConstraintViolationWithTemplate(messageTemplate)?.addConstraintViolation()
            false
        }
    }
}