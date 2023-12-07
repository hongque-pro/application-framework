package com.labijie.application.validation

import com.labijie.application.ApplicationRuntimeException
import com.labijie.application.SpringContext
import com.labijie.application.configuration.ValidationProperties
import com.labijie.application.localeMessage
import com.labijie.infra.utils.ifNullOrBlank
import com.labijie.infra.utils.logger
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.context.i18n.LocaleContextHolder

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-12
 */
class ConfigurablePatternValidator : ConstraintValidator<ConfigurablePattern, String> {

    private lateinit var property: String
    private lateinit var messageTemplate: String
    companion object {
        const val DEFAULT_MESSAGE_TEMPLATE = "{app.validation.pattern.message}"
    }

    private val regex by lazy {
        val config  = SpringContext.current.getBean(ValidationProperties::class.java)
        val value = config.regex[property]
            ?: throw ApplicationRuntimeException("ConfigurablePattern regex property '$property' not found, configure by application.validation.<property name>")
        Regex(value)
    }

    override fun initialize(constraintAnnotation: ConfigurablePattern) {
        super.initialize(constraintAnnotation)
        property = constraintAnnotation.regexConfigName
        val message = constraintAnnotation.message
        messageTemplate = if(message.isBlank() || message == DEFAULT_MESSAGE_TEMPLATE) {
            val config = property.replace("-", "").replace("_", "")
            "{app.validation.pattern.${config.lowercase()}.message}"
        }else {
            constraintAnnotation.message
        }
        messageTemplate = ValidationUtils.localeMessage(messageTemplate, "Invalid parameter format.")
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        return if(value.isNullOrBlank() || regex.matches(value)) {
            true
        }else {
            context?.disableDefaultConstraintViolation()
            context?.buildConstraintViolationWithTemplate(messageTemplate)?.addConstraintViolation()
            false
        }
    }
}