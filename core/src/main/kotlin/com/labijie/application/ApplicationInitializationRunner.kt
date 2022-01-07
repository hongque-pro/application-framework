package com.labijie.application

import com.labijie.application.component.IHumanChecker
import com.labijie.application.component.IMessageSender
import com.labijie.application.component.IObjectStorage
import com.labijie.application.jackson.DescribeEnumDeserializer
import com.labijie.application.jackson.DescribeEnumSerializer
import com.labijie.infra.json.JacksonHelper
import com.labijie.infra.spring.configuration.getApplicationName
import com.labijie.infra.utils.ifNullOrBlank
import com.labijie.infra.utils.logger
import com.labijie.infra.utils.toLocalDateTime
import org.springframework.beans.BeansException
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.info.GitProperties
import org.springframework.boot.web.context.WebServerInitializedEvent
import org.springframework.boot.web.server.WebServer
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationListener
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
    private val contextClass: KClass<T>
) : ApplicationContextAware, ApplicationListener<WebServerInitializedEvent>, Ordered {
    override fun getOrder(): Int {
        return Int.MAX_VALUE
    }

    private lateinit var applicationContext: ApplicationContext
    private lateinit var applicationName: String
    private lateinit var profiles: String
    private lateinit var environment: Environment
    private lateinit var errorRegistrations: ObjectProvider<IErrorRegistration>
    private lateinit var errorRegistry: IErrorRegistry
    private lateinit var gitProperties: GitProperties


    override fun setApplicationContext(applicationContext: ApplicationContext) {
        SpringContext.current = applicationContext
        environment = applicationContext.environment
        this.applicationContext = applicationContext
        applicationName = environment.getApplicationName()
        profiles = applicationContext.environment.activeProfiles.joinToString()
        errorRegistrations = applicationContext.getBeanProvider(IErrorRegistration::class.java)
        errorRegistry = applicationContext.getBean(IErrorRegistry::class.java)
        gitProperties = applicationContext.getBean(GitProperties::class.java)
    }


    private fun initJackson() {
        val enumModule = com.fasterxml.jackson.databind.module.SimpleModule("eumn-module")
        enumModule.addDeserializer(Enum::class.java, DescribeEnumDeserializer())
        enumModule.addSerializer(Enum::class.java, DescribeEnumSerializer)

        JacksonHelper.webCompatibilityMapper.registerModule(enumModule)

        JacksonHelper.defaultObjectMapper.registerModule(enumModule)
    }

    @EventListener(ApplicationReadyEvent::class)
    fun run(event: ApplicationReadyEvent) {
        if (!contextClass.isInstance(event.applicationContext)) {
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
            moduleClass.declaredMethods.firstOrNull { it.name == "initialize" }
        } catch (e: NoSuchMethodException) {
            null
        }
    }

    private var webServer: WebServer? = null


    private val isWebEnvironment by lazy {
        (applicationContext is WebApplicationContext)
    }

    override fun onApplicationEvent(event: WebServerInitializedEvent) {
        webServer = event.webServer
    }

    private fun printComponentImplements(vararg beanTypes: KClass<*>): String{
        if(beanTypes.isNotEmpty()) {
            return StringBuilder().apply {
                appendLine("framework components: ")
                beanTypes.forEach {
                    val bean = applicationContext.getBean(it.java)
                    appendLine(" ${it.java.simpleName}: ${ClassUtils.getShortName(bean::class.java)}")
                }
            }.toString()
        }
        return ""
    }

    private fun reportApplicationStatus(modules: List<Class<*>>) {
        val moduleList = modules.joinToString { it.simpleName.substringBefore("ModuleInitializer") }
        println(
            """
[${this.applicationName}] has been started !! 
framework ver: ${gitProperties.get("build.version")}   
framework commit: ${gitProperties.commitTime.toLocalDateTime().toLocalDate()}  
${printComponentImplements(IHumanChecker::class, IObjectStorage::class, IMessageSender::class)}
module loaded:  ${moduleList.ifNullOrBlank("none module")}
current profiles: $profiles
--------------------------------------------------------------------
${if (isWebEnvironment) this.buildWebServerInfo() else "Command line application"}
--------------------------------------------------------------------
""".trimIndent()
        )

    }

    private fun buildWebServerInfo(): String {
        val contextPath = if (isWebEnvironment) "/${
            environment.getProperty("server.context-path").orEmpty().trimStart('/').trimEnd('/')
        }" else ""
        val context = if (contextPath == "/") "" else contextPath

        return StringBuilder()
            .apply {
                if (webServer != null) {
                    appendLine("web server: ${webServer!!::class.simpleName}")
                }
            }
            .appendLine("swagger:")
            .appendLine("http://localhost:${webServer?.port ?: 8080}$context/swagger")
            .toString()
    }
}