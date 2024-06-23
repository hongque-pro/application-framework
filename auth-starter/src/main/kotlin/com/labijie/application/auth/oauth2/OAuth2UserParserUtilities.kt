/**
 * @author Anders Xiao
 * @date 2024-06-12
 */
package com.labijie.application.auth.oauth2

import com.labijie.application.identity.exception.UnsupportedLoginProviderException
import com.nimbusds.oauth2.sdk.OAuth2Error
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User


class OAuth2UserParserUtilities : ApplicationContextAware {
    private lateinit var applicationContext: ApplicationContext

    companion object {
        private val logger by lazy {
            LoggerFactory.getLogger(OAuth2UserParserUtilities::class.java)
        }
    }

    private val parsers by lazy {
        applicationContext.getBeanProvider(IOAuth2UserParser::class.java).orderedStream().toList()
    }

    fun parse(client: ClientRegistration, user: OAuth2User): OAuth2UserToken {
        val clientId = client.registrationId
        val p = parsers.firstOrNull { it.isSupported(clientId) }
        val token = p?.parse(user)?.apply {
            this.clientRegistrationId = client.registrationId
            this.id = user.name
        }

        if(token == null) {
            val err = StringBuilder().apply {
                appendLine("Unable to got oauth2 user parser (provider: ${client}) !")
                appendLine("OAuth2User: \n")
                user.attributes.forEach { (k, v) ->
                    appendLine("$k : $v")
                }
            }.toString()
            logger.error(err)
        }

        return token ?: throw OAuth2AuthenticationException(OAuth2Error.SERVER_ERROR_CODE)
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }
}