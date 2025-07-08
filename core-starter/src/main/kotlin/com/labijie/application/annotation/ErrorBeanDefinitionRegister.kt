/**
 * @author Anders Xiao
 * @date 2024-08-19
 */
package com.labijie.application.annotation

import com.fasterxml.jackson.module.kotlin.isKotlinClass
import com.labijie.application.ApplicationErrorRegistration
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.core.type.AnnotationMetadata


class ErrorBeanDefinitionRegister : ImportBeanDefinitionRegistrar {

    private val logger by lazy {
        LoggerFactory.getLogger(ErrorBeanDefinitionRegister::class.java)
    }
    override fun registerBeanDefinitions(metadata: AnnotationMetadata, registry: BeanDefinitionRegistry) {
        val attributes = metadata.getAllAnnotationAttributes(ImportErrorDefinition::class.java.name, false)
        val propertyName = ImportErrorDefinition::classes.name


        val array = attributes?.getFirst(propertyName)
        val objects = mutableSetOf<Any>()
        if(array is Array<*>) {
            array.forEach {
                cls->
                if(cls is Class<*>) {
                    if (cls.isKotlinClass() && cls.kotlin.objectInstance != null) {
                        objects.add(cls.kotlin.objectInstance!!)
                    } else {
                        logger.warn("Error definition '${cls.name}' must be a kotlin object (from ${ImportErrorDefinition::class.java.simpleName} annotation), change from 'class ${cls.simpleName}' to 'object ${cls.simpleName}' to fix this warning.")
                    }
                }
            }
        }

        objects.forEach {
            val builder: BeanDefinitionBuilder =
                BeanDefinitionBuilder.genericBeanDefinition(ApplicationErrorRegistration::class.java)

            builder.addPropertyValue(ApplicationErrorRegistration::errorClassName.name, it::class.java.name)

            val beanName = getBeanName(metadata, it)
            registry.registerBeanDefinition(beanName, builder.beanDefinition)
            logger.debug("Error Bean Definition: $beanName")
        }
    }

    private fun getBeanName(metadata: AnnotationMetadata, errorObject: Any): String {
        return  "${ApplicationErrorRegistration::class.simpleName}#" + errorObject::class.qualifiedName
    }
}