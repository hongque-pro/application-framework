/**
 * @author Anders Xiao
 * @date 2025-05-16
 */
package com.labijie.application.dapr.configuration

import com.labijie.application.JsonMode
import com.labijie.application.dapr.IDaprClientBuildCustomizer
import com.labijie.application.dapr.components.DaprJsonSerializer
import com.labijie.application.dapr.configuration.DaprProperties.Companion.JSON_MODEL_CONFIG_KEY
import com.labijie.application.getEnumFromString
import com.labijie.infra.json.JacksonHelper
import io.dapr.client.DaprClientBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.beans.factory.getBeansOfType
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.core.env.Environment


class ApplicationDaprBeanPostProcessor(environment: Environment) : BeanPostProcessor, ApplicationContextAware {

    private var applicationContext: ApplicationContext? = null

    private val logger by lazy {
        LoggerFactory.getLogger(ApplicationDaprBeanPostProcessor::class.java)
    }

    private val jsonModel by lazy {
       val model = environment.getProperty(JSON_MODEL_CONFIG_KEY)
        getEnumFromString(JsonMode::class.java, model.orEmpty(), ignoreCase = true) ?: JsonMode.NORMAL
    }


    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {

        val model = jsonModel
        if (bean is DaprClientBuilder) {
            val objectMapper =
                if (model == JsonMode.JAVASCRIPT) JacksonHelper.webCompatibilityMapper else JacksonHelper.defaultObjectMapper

            logger.info("Dapr client serialization mode: $model")

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