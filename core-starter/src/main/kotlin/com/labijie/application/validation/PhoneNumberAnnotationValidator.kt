package com.labijie.application.validation

import com.labijie.application.ApplicationErrors
import com.labijie.application.SpringContext
import com.labijie.application.component.IPhoneValidator
import com.labijie.application.component.impl.NationalPhoneValidator
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext
import org.springframework.beans.BeanWrapperImpl
import java.math.BigDecimal

/**
 * @author Anders Xiao
 * @date 2025/8/4
 */
class PhoneNumberAnnotationValidator : ConstraintValidator<PhoneNumber, Any> {

    companion object {
        const val DEFAULT_MESSAGE_TEMPLATE = "{app.err.${ApplicationErrors.InvalidPhoneNumber}}"
    }

    private lateinit var messageTemplate: String

    private var countryField: String = ""
    private var numberField: String = ""

    private val validator by lazy {
        if (SpringContext.isInitialized) {
            SpringContext.current.getBean(IPhoneValidator::class.java)
        } else NationalPhoneValidator()
    }

    private class FullPhoneNumber(val countryCode: Short?, val number: String?, var invalid: Boolean)

    private fun getFullNumber(obj: Any, countryField: String, numberField: String): FullPhoneNumber {
        val wrapper = BeanWrapperImpl(obj)

        var invalid = false
        val country = wrapper.getPropertyValue(countryField)
        val countryCode = when (country) {
            null, is Float, is Double, is BigDecimal -> {
                invalid = true
                null
            }

            is Number -> {
                country.toShort()
            }

            is String -> {
                val code = country.toShortOrNull()
                if (country.isNotBlank() && code == null) {
                    invalid = true
                }
                country.toShortOrNull()
            }

            else -> null
        }
        val secondValue = wrapper.getPropertyValue(numberField)

        val number: String? = (secondValue as? String) ?: secondValue?.toString()


        val p = FullPhoneNumber(countryCode, number, invalid)
        return p
    }

    override fun isValid(value: Any?, context: ConstraintValidatorContext): Boolean {
        if (value == null) return true

//        val hibernateCtx = context.unwrap(HibernateConstraintValidatorContext::class.java)
//
//        val rootBean = hibernateCtx.getConstraintValidatorPayload(Any::class.java)

        val full = getFullNumber(value, countryField, numberField)

        val passNull = full.countryCode == null && full.number.isNullOrBlank()

        return if (passNull || (!full.invalid && validator.validate(
                full.countryCode ?: 0,
                full.number.orEmpty(),
                false
            ))
        ) {
            true
        } else {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate(messageTemplate)?.addPropertyNode(countryField)
                ?.addConstraintViolation()
            context.buildConstraintViolationWithTemplate(messageTemplate)?.addPropertyNode(numberField)
                ?.addConstraintViolation()
            false
        }
    }

    override fun initialize(constraintAnnotation: PhoneNumber) {
        super.initialize(constraintAnnotation)

        countryField = constraintAnnotation.countryCodeField
        numberField = constraintAnnotation.numberField

        if (countryField.isBlank()) {
            throw IllegalArgumentException("'${PhoneNumber::countryCodeField.name}' in annotation PhoneNumber can not be blank")
        }
        if (numberField.isBlank()) {
            throw IllegalArgumentException("'${PhoneNumber::numberField.name}' in annotation PhoneNumber can not be blank")
        }

        val message = constraintAnnotation.message
        val format =
            if (message.isBlank()) {
                DEFAULT_MESSAGE_TEMPLATE
            } else {
                constraintAnnotation.message
            }
        messageTemplate = ValidationUtils.localeMessage(format, "Invalid phone number.")
    }
}