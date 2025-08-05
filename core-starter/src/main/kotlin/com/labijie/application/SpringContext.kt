package com.labijie.application

import com.labijie.application.annotation.GradleApplication
import com.labijie.application.service.ILocalizationService
import com.labijie.infra.isDevelopment
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.info.GitProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.support.MessageSourceAccessor

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-12
 */
object SpringContext {
    lateinit var current: ApplicationContext

    val isInitialized: Boolean
        get() = SpringContext::current.isInitialized

    val isDevelopment by lazy {
        isInitialized && current.environment.isDevelopment
    }

    val isTest by lazy {
        isInitialized && current.environment.activeProfiles.contains("test")
    }

    val localizationService by lazy {
        current.getBeanProvider(ILocalizationService::class.java)
    }

    val errorRegistry: IErrorRegistry by lazy {
        current.getBean(IErrorRegistry::class.java)
    }

    val messageSourceAccessor by lazy {
        MessageSourceAccessor(current)
    }

    fun ApplicationContext.findApplicationMainClass(): Any? {
        if (isInitialized) {
            val springBootAppBeanName = this.getBeanNamesForAnnotation(SpringBootApplication::class.java)
            return if (springBootAppBeanName.isNotEmpty() && !springBootAppBeanName[0].isNullOrBlank()) {
                current.getBean(springBootAppBeanName[0])
            } else null
        }
        return null
    }

    private val applicationGitPropertiesSync = Any()
    private var applicationGitProperties: GitProperties? = null
    private var applicationGitPropertiesLoaded = false

    fun ApplicationContext.getApplicationGitProperties(): GitProperties? {
        if(!isInitialized) {
            return null
        }
        if(applicationGitPropertiesLoaded) {
            return applicationGitProperties
        }
        else {
            synchronized(applicationGitPropertiesSync) {
                if(!applicationGitPropertiesLoaded) {
                    val main = findApplicationMainClass()
                    if (main != null) {
                        val anno = main::class.java.getAnnotation(GradleApplication::class.java)
                        if (anno != null && !anno.projectGroup.isBlank()) {
                            val properties = getGitProperties(main::class.java) {
                                anno.projectGroup.equals(it.getProperty("project.group"), ignoreCase = true) &&
                                        (anno.projectName.isBlank() || anno.projectName.equals(
                                            it.getProperty("project.name"),
                                            ignoreCase = true
                                        ))
                            }
                            if (properties != null) {
                                applicationGitProperties = properties
                                applicationGitPropertiesLoaded = true
                            }
                        }
                    }

                    if(!applicationGitPropertiesLoaded) {
                        applicationGitProperties = main?.let { getGitProperties(it::class.java) }
                        applicationGitPropertiesLoaded = true
                    }
                }
            }
        }

        return applicationGitProperties
    }

}