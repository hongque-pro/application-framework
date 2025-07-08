package com.labijie.application.aot

import com.labijie.application.ApplicationErrorRegistration
import com.labijie.infra.utils.ifNullOrBlank
import org.slf4j.LoggerFactory
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.TypeReference
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory


class ImportErrorDefinitionAotProcessor : BeanFactoryInitializationAotProcessor {

    companion object {
        private val logger by lazy {
            LoggerFactory.getLogger(ImportErrorDefinitionAotProcessor::class.java)
        }
    }

    override fun processAheadOfTime(beanFactory: ConfigurableListableBeanFactory): BeanFactoryInitializationAotContribution? {
        val errorRegistrations = collectAnnotatedErrorRegistrationBeanDefines(beanFactory)


        val objectClasses = mutableSetOf<String>()
        errorRegistrations.forEach { it ->
            val errorClass = it.value.propertyValues.get(ApplicationErrorRegistration::errorClassName.name)?.toString()
            logger.debug("Aot process for error definition: ${errorClass.ifNullOrBlank { "null" }}")
            errorClass?.let {
                objectClasses.add(errorClass)
            }
        }

        if(objectClasses.isNotEmpty()) {
            return BeanFactoryInitializationAotContribution { generationContext, code ->
                val hints = generationContext.runtimeHints
                for (err in objectClasses) {
                    hints.reflection().registerType(TypeReference.of(err)) {
                        it.withMembers(
                            MemberCategory.INVOKE_DECLARED_METHODS,
                            MemberCategory.DECLARED_FIELDS,
                            MemberCategory.INTROSPECT_DECLARED_METHODS,
                        )
                    }
                }
            }
        }
        return null
    }


    private fun collectAnnotatedErrorRegistrationBeanDefines(
        beanFactory: ConfigurableListableBeanFactory
    ): Map<String, BeanDefinition> {
        return beanFactory.beanDefinitionNames
            .mapNotNull { beanName ->
                beanFactory.getBeanDefinition(beanName).let { bd ->
                    if(bd.beanClassName == ApplicationErrorRegistration::class.java.name) Pair(beanName, bd) else null
                }
            }.toMap()
    }
}