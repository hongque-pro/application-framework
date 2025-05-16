/**
 * @author Anders Xiao
 * @date 2025-05-16
 */
package com.labijie.application.dapr.configuration

import com.labijie.application.JsonMode
import com.labijie.application.dapr.IDaprClientBuildCustomizer
import com.labijie.application.dapr.components.DaprJsonSerializer
import com.labijie.infra.json.JacksonHelper
import io.dapr.client.DaprClient
import io.dapr.client.DaprClientBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware


class ApplicationDaprBeanPostProcessor(
    private val daprProperties: DaprProperties) : BeanPostProcessor, ApplicationContextAware {

    private var applicationContext: ApplicationContext? = null

    private val logger by lazy {
        LoggerFactory.getLogger(ApplicationDaprBeanPostProcessor::class.java)
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        if(bean is DaprClientBuilder)
        {
            val objectMapper =
                if (daprProperties.jsonMode == JsonMode.JAVASCRIPT) JacksonHelper.webCompatibilityMapper else JacksonHelper.defaultObjectMapper

            logger.info("Dapr client serialization mode: ${daprProperties.jsonMode}")

            val objectSerializer = DaprJsonSerializer(objectMapper)
            bean.withObjectSerializer(objectSerializer)
            val customizers = applicationContext?.getBeanProvider(IDaprClientBuildCustomizer::class.java)
            customizers?.orderedStream()?.forEach {
                it.customize(bean)
            }
        }

        return bean
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }
}