/**
 * @author Anders Xiao
 * @date 2023-12-12
 */
package com.labijie.application.auth.component

import com.labijie.application.auth.configuration.DefaultUserCreationProperties
import com.labijie.application.identity.IdentityUtils
import com.labijie.application.identity.service.IUserService
import com.labijie.infra.IIdGenerator
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware


class DefaultUserInitializer(private val properties: DefaultUserCreationProperties): CommandLineRunner, ApplicationContextAware {

    private lateinit var applicationContext: ApplicationContext

    private val idGenerator: IIdGenerator by lazy {
        applicationContext.getBean(IIdGenerator::class.java)
    }

    private val userService: IUserService by lazy {
        applicationContext.getBean(IUserService::class.java)
    }

    companion object {
        private val logger by lazy {
            LoggerFactory.getLogger(DefaultUserInitializer::class.java)
        }
    }

    override fun run(vararg args: String?) {
        if (properties.enabled) {
            if (properties.password.isBlank()) {
                return logger.warn("Default user creation enabled, but default user password was not configured, skip create default user.")
            }
            val u = userService.getUser(properties.username)
            if (u == null) {
                val user = IdentityUtils.createUser(idGenerator.newId(), properties.username, 0)
                val roles = properties.roles.split(",").map { it.trim() }.toSet()
                val r = userService.createUser(user, properties.password, roles)
                logger.info("Default user created, user name: ${user.userName}, id: ${r.user.id}")
            }
        }
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }
}