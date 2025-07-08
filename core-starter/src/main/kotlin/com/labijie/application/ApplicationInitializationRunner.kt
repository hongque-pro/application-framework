package com.labijie.application

import com.labijie.application.SpringContext.getApplicationGitProperties
import com.labijie.application.component.IBootPrinter
import com.labijie.application.component.IHumanChecker
import com.labijie.application.component.IMessageService
import com.labijie.application.component.IObjectStorage
import com.labijie.application.configuration.ApplicationCoreProperties
import com.labijie.application.configuration.HttpClientProperties
import com.labijie.application.httpclient.HttpClientLoggingInterceptor
import com.labijie.application.jackson.DescribeEnumDeserializer
import com.labijie.application.jackson.DescribeEnumSerializer
import com.labijie.application.jackson.IObjectMapperCustomizer
import com.labijie.application.localization.ILocalizationResourceBundle
import com.labijie.application.localization.LocalizationMessageSource
import com.labijie.application.service.ILocalizationService
import com.labijie.caching.ICacheManager
import com.labijie.infra.getApplicationName
import com.labijie.infra.json.JacksonHelper
import com.labijie.infra.orm.withoutSqlLog
import com.labijie.infra.utils.ifNullOrBlank
import com.labijie.infra.utils.logger
import com.labijie.infra.utils.throwIfNecessary
import com.labijie.infra.utils.toLocalDateTime
import com.sun.management.OperatingSystemMXBean
import org.apache.commons.lang3.LocaleUtils
import org.springframework.beans.BeansException
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.WebApplicationType
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.context.logging.LoggingApplicationListener.LOGGING_SYSTEM_BEAN_NAME
import org.springframework.boot.logging.LogLevel
import org.springframework.boot.logging.LoggingSystem
import org.springframework.boot.web.context.WebServerInitializedEvent
import org.springframework.boot.web.server.WebServer
import org.springframework.context.*
import org.springframework.context.event.EventListener
import org.springframework.core.Ordered
import org.springframework.core.env.Environment
import org.springframework.util.ClassUtils
import org.springframework.web.context.WebApplicationContext
import java.lang.management.ManagementFactory
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass
import kotlin.system.exitProcess


/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-11
 */
class ApplicationInitializationRunner(
    private val applicationType: WebApplicationType
) : ApplicationContextAware, ApplicationListener<WebServerInitializedEvent>, Ordered {
    override fun getOrder(): Int {
        return Int.MAX_VALUE
    }

    private lateinit var applicationContext: ApplicationContext
    private lateinit var applicationName: String
    private lateinit var profiles: String
    private lateinit var environment: Environment
    private lateinit var errorRegistrations: ObjectProvider<ApplicationErrorRegistration>
    private lateinit var errorRegistry: IErrorRegistry

    private val loggingSystem by lazy {
        applicationContext.getBean(LOGGING_SYSTEM_BEAN_NAME, LoggingSystem::class.java)
    }

    private val localizationService by lazy {
        applicationContext.getBean(ILocalizationService::class.java)
    }

    private val applicationCoreProperties by lazy {
        applicationContext.getBean(ApplicationCoreProperties::class.java)
    }

    private var httpClientProperties: HttpClientProperties? = null


    override fun setApplicationContext(applicationContext: ApplicationContext) {
        SpringContext.current = applicationContext
        environment = applicationContext.environment
        this.applicationContext = applicationContext
        applicationName = environment.getApplicationName(false)
        profiles = applicationContext.environment.activeProfiles.joinToString()
        errorRegistrations = applicationContext.getBeanProvider(ApplicationErrorRegistration::class.java)
        errorRegistry = applicationContext.getBean(IErrorRegistry::class.java)
        httpClientProperties = applicationContext.getBeanProvider(HttpClientProperties::class.java).ifAvailable
    }


    private fun initJackson() {
        val enumModule = com.fasterxml.jackson.databind.module.SimpleModule("eumn-module")
        enumModule.addDeserializer(Enum::class.java, DescribeEnumDeserializer())
        enumModule.addSerializer(Enum::class.java, DescribeEnumSerializer)

        JacksonHelper.webCompatibilityMapper.registerModule(enumModule)
        JacksonHelper.defaultObjectMapper.registerModule(enumModule)


        applicationContext.getBeanProvider(IObjectMapperCustomizer::class.java).orderedStream().forEach {
            it.customize(JacksonHelper.webCompatibilityMapper)
            it.customize(JacksonHelper.defaultObjectMapper)
        }
    }

    private fun initLocalization() {
        withoutSqlLog {
            val locales = applicationCoreProperties.preloadLocales
            val messageSource =
                applicationContext.getBeanProvider(MessageSource::class.java).ifAvailable as? LocalizationMessageSource
            if (locales.isNotBlank() && messageSource != null) {
                val localeList = locales.split(",").mapNotNull {
                    try {
                        LocaleUtils.toLocale(it.trim())
                    } catch (_: IllegalArgumentException) {
                        null
                    }
                }


                val bundleList =
                    applicationContext.getBeanProvider(ILocalizationResourceBundle::class.java).orderedStream().toList()
                        .flatMap { b ->
                            b.getResources()
                        }.toTypedArray()

                messageSource.loadResourceBundle(*bundleList)
                try {
                    messageSource.saveMessagesFromExistedBundle(*localeList.toTypedArray())
                } catch (e: Throwable) {
                    e.throwIfNecessary()
                    logger.error("Save locale message bundle to jdbc failed")
                }
            }
        }

    }

    private fun initHttpClientLogger() {
        if (httpClientProperties?.loggerEnabled == true) {
            loggingSystem.setLogLevel(HttpClientLoggingInterceptor::class.java.name, LogLevel.DEBUG)
        }
    }

    private fun initErrorRegistrations() {
        try {
            withoutSqlLog {
                errorRegistrations.orderedStream().forEach {
                    it.register(errorRegistry, localizationService)
                }
            }
            errorRegistry.persistMessages()

        } catch (e: Throwable) {
            logger.error("Application '$applicationName' initialize fault.", e)
            exitProcess(-3333)
        }
    }

    @EventListener(ApplicationReadyEvent::class)
    fun run(event: ApplicationReadyEvent) {

        this.initJackson()
        this.initHttpClientLogger()
        this.initLocalization()
        initErrorRegistrations()
        loadModules()
    }

    private fun loadModules() {
        val printer = this.applicationContext.getBeanProvider(IBootPrinter::class.java).orderedStream().toList()
        val modules = this.applicationContext.getBeanProvider(IModuleInitializer::class.java).orderedStream().map {
            val moduleClass = it::class.java
            val method = tryGetMethod(moduleClass)
            if (method == null) {
                if (logger.isDebugEnabled) {
                    logger.warn("Find bean implements this interface (bean type: ${moduleClass.name})., but no have 'initialize' method")
                }
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
            it
        }

        val list = modules.toList()
        this.reportApplicationStatus(list, printer)
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

    private fun printSystemInfo(): String {

        val byteToMB = (1024 * 1024)
        val rt = Runtime.getRuntime()
        val vmTotal = rt.totalMemory() / byteToMB
        val vmFree = rt.freeMemory() / byteToMB
        val vmMax = rt.maxMemory() / byteToMB
        val vmUse = vmTotal - vmFree

        val osmxb = ManagementFactory.getOperatingSystemMXBean() as OperatingSystemMXBean
        val os = System.getProperty("os.name")
        val physicalFree: Long = osmxb.freeMemorySize / byteToMB
        val physicalTotal: Long = osmxb.totalMemorySize / byteToMB
        val physicalUse = physicalTotal - physicalFree

        val keyPadding = 16
        val valuePadding = 24
        val len = keyPadding + valuePadding
        val osMap = mapOf(
            "Host OS: " to os,
            "Host in used:".padEnd(keyPadding) to "$physicalUse MB (${
                String.format(
                    "%.2f",
                    physicalUse / 1024.0
                )
            } GB)".padStart(valuePadding),
            "Host free:".padEnd(keyPadding) to "$physicalFree MB (${
                String.format(
                    "%.2f",
                    physicalFree / 1024.0
                )
            } GB)".padStart(valuePadding),
            "Host totals:".padEnd(keyPadding) to "$physicalUse MB (${
                String.format(
                    "%.2f",
                    physicalTotal / 1024.0
                )
            } GB)".padStart(valuePadding)
        )

        val jvm = mapOf(
            "JVM in used:".padEnd(keyPadding) to "$vmUse MB (${String.format("%.2f", vmUse / 1024.0)} GB)".padStart(
                valuePadding
            ),
            "JVM free:".padEnd(keyPadding) to "$vmFree MB (${String.format("%.2f", vmFree / 1024.0)} GB)".padStart(
                valuePadding
            ),
            "JVM max:".padEnd(keyPadding) to "$vmMax MB (${String.format("%.2f", vmMax / 1024.0)} GB)".padStart(
                valuePadding
            ),
            "JVM totals:".padEnd(keyPadding) to "$vmTotal MB (${String.format("%.2f", vmTotal / 1024.0)} GB)".padStart(
                valuePadding
            )
        )
        val osTitle = " HOST MEMORY INFO "
        val jvmTitle = " JVM MEMORY INFO "
        return StringBuilder().apply {
            appendLine(osTitle.paddingLeftAndRight(len, '-'))
            appendLine(osMap.map { "${it.key}${it.value}" }.joinToString(System.lineSeparator()))
            appendLine()
            appendLine(jvmTitle.paddingLeftAndRight(len, '-'))
            appendLine(jvm.map { "${it.key}${it.value}" }.joinToString(System.lineSeparator()))
        }.toString()
    }

    private fun printComponentImplements(vararg beanTypes: KClass<*>): String {
        if (beanTypes.isNotEmpty()) {
            return StringBuilder().apply {
                appendLine("framework components: ")
                beanTypes.forEach { benType ->
                    val bean =
                        applicationContext.getBeanProvider(benType.java).ifAvailable?.let { ClassUtils.getShortName(it::class.java) }
                    appendLine(" ${benType.java.simpleName}: ${bean.ifNullOrBlank { "<null>" }}")
                }
                try {
                    val caching = applicationContext.getBeansOfType(ICacheManager::class.java).firstNotNullOf {
                        it.key
                    }
                    appendLine("caching implement: $caching")
                } catch (_: BeansException) {

                }

            }.toString()
        }
        return ""
    }

    private fun reportApplicationStatus(modules: List<IModuleInitializer>, printers: List<IBootPrinter>) {
        val moduleList = modules.joinToString { it.getModuleName() }
        val gitProperties = getGitProperties(this::class.java) {
            val group = it.getProperty("project.group") ?: ""
            group == "com.labijie.application"
        }

        val sb = StringBuilder().also { builder ->
            builder.appendLine(
                """
Application '${this.applicationName}' has been started !! 
${applicationContext.getApplicationGitProperties()?.buildVersion ?: ""}

${printSystemInfo()}
framework version: ${gitProperties?.get("build.version")}   
framework commit: ${gitProperties?.commitTime?.toLocalDateTime()?.toLocalDate()}  
${
                    printComponentImplements(
                        IHumanChecker::class,
                        IObjectStorage::class,
                        IMessageService::class,
                        ILocalizationService::class
                    )
                }
module loaded:  ${moduleList.ifNullOrBlank("<none>")}
current profiles: $profiles
--------------------------------------------------------------------
""".trimIndent()
            )

            printers.forEach {
                it.appendBootMessages(builder)
            }

            builder.appendLine(
                """
--------------------------------------------------------------------
${if (isWebEnvironment) this.buildWebServerInfo() else "Command line application"}
--------------------------------------------------------------------
                """.trimIndent()
            )
        }

        println(sb.toString())

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