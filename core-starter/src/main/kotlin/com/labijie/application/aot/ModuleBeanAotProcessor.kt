package com.labijie.application.aot

import com.labijie.application.IModuleInitializer
import org.slf4j.LoggerFactory
import org.springframework.aot.hint.ExecutableMode
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory

/**
 * @author Anders Xiao
 * @date 2025/6/21
 */
class ModuleBeanAotProcessor : BeanFactoryInitializationAotProcessor {


    companion object {
        private val logger by lazy {
            LoggerFactory.getLogger(ImportErrorDefinitionAotProcessor::class.java)
        }
    }

    override fun processAheadOfTime(beanFactory: ConfigurableListableBeanFactory): BeanFactoryInitializationAotContribution? {
        val moduleBeans = collectBeanDefines(beanFactory)

        if(moduleBeans.isNotEmpty()) {
            return BeanFactoryInitializationAotContribution{ generationContext, code ->
                val hints = generationContext.runtimeHints
                for (beanName in moduleBeans) {
                    beanFactory.getType(beanName)?.let {
                        beanClass->
                        beanClass.declaredMethods.firstOrNull { it.name == "initialize" }?.let {
                            hints.reflection().registerMethod(it, ExecutableMode.INVOKE)
                        }
                        logger.info("Aot process for module class: ${beanClass.simpleName}")
                    }
                }

            }
        }
        return null
    }

    private fun collectBeanDefines(
        beanFactory: ConfigurableListableBeanFactory
    ): Set<String> {

       val names = beanFactory.getBeanNamesForType(IModuleInitializer::class.java)
        return names.toSet()
    }
}