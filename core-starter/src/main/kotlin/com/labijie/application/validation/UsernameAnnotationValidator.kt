/**
 * @author Anders Xiao
 * @date 2024-06-04
 */
package com.labijie.application.validation

import com.labijie.application.ApplicationErrors
import com.labijie.application.SpringContext
import com.labijie.application.component.IUserNameValidator
import com.labijie.application.localeErrorMessage
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext


class UsernameAnnotationValidator: ConstraintValidator<Username, String> {

    companion object {
        const val DEFAULT_MESSAGE_TEMPLATE = "{app.err.invalid_username}"
    }

    private lateinit var messageTemplate: String

    private val validator by lazy {
        SpringContext.current.getBean(IUserNameValidator::class.java)
    }


    override fun initialize(constraintAnnotation: Username) {
        super.initialize(constraintAnnotation)

        val message = constraintAnnotation.message
        val format =
            if (message.isBlank() || message == DEFAULT_MESSAGE_TEMPLATE) {
                "{app.err.invalid_username}"
            } else {
                constraintAnnotation.message
            }
        messageTemplate = ValidationUtils.localeMessage(format,"Invalid user name.")
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