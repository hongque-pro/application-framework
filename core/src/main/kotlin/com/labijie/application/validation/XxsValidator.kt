package com.labijie.application.validation

import io.netty.util.internal.ResourcesUtil
import com.labijie.infra.utils.logger
import org.owasp.validator.html.AntiSamy
import org.owasp.validator.html.Policy
import org.owasp.validator.html.PolicyException
import java.io.FileNotFoundException
import java.net.URL
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import org.springframework.core.io.ClassPathResource



/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-11-13
 */

class XxsValidator : ConstraintValidator<XxsReject, String> {
    companion object{
        @JvmStatic
        private val simplePolicy: Policy by lazy {
            val resource = ClassPathResource("/antisamy-simple.xml", XxsValidator::class.java)
            resource.inputStream.use {
                Policy.getInstance(it)
            }
        }

        @JvmStatic
        private val strictPolicy: Policy by lazy {
            val resource = ClassPathResource("/antisamy-strict.xml", XxsValidator::class.java)
            resource.inputStream.use {
                Policy.getInstance(it)
            }
        }

        @JvmStatic
        private val antiSamy:AntiSamy by lazy { AntiSamy() }
    }

    private var currentPolicy: Policy = strictPolicy

    override fun initialize(constraintAnnotation: XxsReject) {
        currentPolicy = when(constraintAnnotation.policy){
            XxsDefinePolicy.Simple-> simplePolicy
            XxsDefinePolicy.Strict-> strictPolicy
        }
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        return if (value.isNullOrBlank()) true else {
            return try {
                val result = antiSamy.scan(value, currentPolicy)
                if(result.numberOfErrors > 0){
                    logger.warn("""
                    ==============Xxs validation fault start================
                    ${result.errorMessages.joinToString(System.lineSeparator())}
                    ==============Xxs validation fault end ================
                """.trimIndent())
                }
                return result.numberOfErrors == 0
            }catch (e:PolicyException){
                logger.error("Xxs injection detected.", e)
                false
            }
        }
    }
}