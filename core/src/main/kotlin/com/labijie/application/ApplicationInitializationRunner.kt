package com.labijie.application

import com.labijie.application.jackson.DescribeEnumDeserializer
import com.labijie.application.jackson.DescribeEnumSerializer
import com.labijie.infra.json.JacksonHelper
import com.labijie.infra.spring.configuration.getApplicationName
import com.labijie.infra.utils.ifNullOrBlank
import com.labijie.infra.utils.logger
import org.springframework.beans.BeansException
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.event.EventListener
import org.springframework.core.Ordered
import org.springframework.core.env.Environment
import org.springframework.util.ClassUtils
import org.springframework.web.context.WebApplicationContext
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import kotlin.concurrent.thread
import kotlin.reflect.KClass
import kotlin.streams.toList
import kotlin.system.exitProcess

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-11
 */
class ApplicationInitializationRunner<T : ConfigurableApplicationContext>(
        private val contextClass: KClass<T>) : ApplicationContextAware, Ordered {
    override fun getOrder(): Int {
        return Int.MAX_VALUE
    }

    private lateinit var applicationContext: ApplicationContext
    private lateinit var applicationName: String
    private lateinit var profiles: String
    private lateinit var environment: Environment
    private lateinit var errorRegistrations: ObjectProvider<IErrorRegistration>
    private lateinit var errorRegistry: IErrorRegistry


    override fun setApplicationContext(applicationContext: ApplicationContext) {
        SpringContext.current = applicationContext
        environment = applicationContext.environment
        this.applicationContext = applicationContext
        applicationName = environment.getApplicationName()
        profiles = applicationContext.environment.activeProfiles.joinToString()
        errorRegistrations = applicationContext.getBeanProvider(IErrorRegistration::class.java)
        errorRegistry = applicationContext.getBean(IErrorRegistry::class.java)
    }



    private val isWebEnvironment by lazy {
        (applicationContext is WebApplicationContext)
    }

    private fun initJackson(){
        val eumnModule  = com.fasterxml.jackson.databind.module.SimpleModule("eumn-module")
        eumnModule.addDeserializer(Enum::class.java, DescribeEnumDeserializer())
        eumnModule.addSerializer(Enum::class.java, DescribeEnumSerializer)

        JacksonHelper.webCompatibilityMapper.registerModule(eumnModule)

        JacksonHelper.defaultObjectMapper.registerModule(eumnModule)
    }

    @EventListener(ApplicationReadyEvent::class)
    fun run(event: ApplicationReadyEvent) {
        if(!contextClass.isInstance(event.applicationContext)){
            return
        }
        this.initJackson()
        try {
            loadModules()
            thread {
                errorRegistry.registerErrors(ApplicationErrors)
                errorRegistrations.orderedStream().forEach {
                    it.register(errorRegistry)
                }
            }
        } catch (e: Throwable) {
            logger.error("Application '$applicationName' initialize fault.", e)
            exitProcess(-3333)
        }

    }

    private fun loadModules() {
        val modules = this.applicationContext.getBeanProvider(IModuleInitializer::class.java).orderedStream().map {
            val moduleClass = it::class.java
            val method = tryGetMethod(moduleClass)
            if (method == null) {
                logger.warn("Find bean implements this interface (bean type: ${moduleClass.name})., but no have 'initialize' method")
            } else {
                try {
                    val parameters = method.parameters.map { param ->
                        val isObjectProvider =
                            param.type == ObjectProvider::class.java && param.parameterizedType != null && param.parameterizedType is ParameterizedType
                        if (isObjectProvider) {
                            val type = (param.parameterizedType as ParameterizedType).actualTypeArguments[0]
                            applicationContext.getBeanProvider(type as Class<*>)
                        } else {
                            applicationContext.getBean(param.type)
                        }
                    }
                    method.invoke(it, *parameters.toTypedArray())
                } catch (e: BeansException) {
                    val error =
                        "Process data initialization fault ( initializer:  ${moduleClass.name} , app: '$applicationName')"
                    throw RuntimeException(error, e)
                }
            }
            moduleClass
        }

        val list = modules.toList()
        this.reportApplicationStatus(list)
    }

    private fun tryGetMethod(moduleClass: Class<out IModuleInitializer>): Method? {
        return try {
            moduleClass.declaredMethods.firstOrNull {it.name == "initialize"}
        } catch (e: NoSuchMethodException) {
            null
        }
    }

    private fun reportApplicationStatus(modules: List<Class<*>>) {
        val contextPath = if(isWebEnvironment) "/${environment.getProperty("server.context-path").orEmpty().trimStart('/').trimEnd('/')}" else ""
        val context = if(contextPath == "/") "" else contextPath
        val moduleList = modules.joinToString { it.simpleName.substringBefore("ModuleInitializer") }
        println(
                """
        [${this.applicationName}] has been started !! 
        
        current profiles: $profiles
        module loaded:  ${moduleList.ifNullOrBlank("none module")}
        --------------------------------------------------------------------
        ${if (isWebEnvironment) "http://localhost:${environment.getProperty("server.port").ifNullOrBlank("80")}$context/swagger-ui/index.html" else "Command line application"}
        --------------------------------------------------------------------
        """
        )
    }
}