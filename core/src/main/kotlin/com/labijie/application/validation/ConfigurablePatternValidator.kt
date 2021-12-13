package com.labijie.application.validation

import com.labijie.application.ApplicationRuntimeException
import com.labijie.application.SpringContext
import com.labijie.application.configuration.ValidationConfiguration
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-12
 */
class ConfigurablePatternValidator : ConstraintValidator<ConfigurablePattern, String> {

    private lateinit var property: String

    private val regex by lazy {
        val config  = SpringContext.current.getBean(ValidationConfiguration::class.java)
        val value = config.regex[property]
            ?: throw ApplicationRuntimeException("ConfigurablePattern regex property '$property' not found")
        Regex(value)
    }

    override fun initialize(constraintAnnotation: ConfigurablePattern) {
        super.initialize(constraintAnnotation)
        property = constraintAnnotation.regexProperty
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        return if (value == null) true else regex.matches(value)
    }
}