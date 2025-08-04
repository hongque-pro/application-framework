/**
 * @author Anders Xiao
 * @date 2025-05-16
 */
package com.labijie.application.dapr.configuration

import com.labijie.application.JsonMode
import com.labijie.application.dapr.IDaprClientBuildCustomizer
import com.labijie.application.dapr.components.DaprJsonSerializer
import com.labijie.infra.json.JacksonHelper
import io.dapr.client.DaprClientBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware


class ApplicationDaprBeanPostProcessor() : BeanPostProcessor, ApplicationContextAware {

    private var applicationContext: ApplicationContext? = null

    private val logger by lazy {
        LoggerFactory.getLogger(ApplicationDaprBeanPostProcessor::class.java)
    }

    private val daprProperties by lazy {
        applicationContext?.getBean<DaprProperties>()
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        daprProperties?.let {
            properties ->
            if (bean is DaprClientBuilder) {
                val objectMapper =
                    if (properties.jsonMode == JsonMode.JAVASCRIPT) JacksonHelper.webCompatibilityMapper else JacksonHelper.defaultObjectMapper

                logger.info("Dapr client serialization mode: ${properties.jsonMode}")

                val objectSerializer = DaprJsonSerializer(objectMapper)
                bean.withObjectSerializer(objectSerializer)
                val customizers = applicationContext?.getBeanProvider(IDaprClientBuildCustomizer::class.java)
                customizers?.orderedStream()?.forEach {
                    it.customize(bean)
                }
            }
        }

        return bean
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }
}