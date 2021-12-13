package com.labijie.application.auth.component

import com.labijie.application.identity.data.OAuth2ClientDetailsRecord
import com.labijie.application.identity.service.IOAuth2ClientService
import com.labijie.infra.oauth2.configuration.OAuth2ServerProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.TokenSettings
import org.springframework.util.StringUtils
import java.time.Duration

/**
 *
 * @Author: Anders Xiao
 * @Date: 2021/12/13
 * @Description:
 */
class DefaultClientRepository(
    private val clientService: IOAuth2ClientService
) : RegisteredClientRepository, ApplicationContextAware {

    private var oauth2ServerProperties: OAuth2ServerProperties? = null

    override fun save(registeredClient: RegisteredClient?) {
        val client = registeredClient ?: throw java.lang.IllegalArgumentException("registeredClient can not be null")
        if (registeredClient.id != registeredClient.clientId) {
            throw IllegalArgumentException("registeredClient id and client id must be same")
        }

        val r = this.findById(registeredClient.id)
        if (r == null) {

            val record = OAuth2ClientDetailsRecord().apply {
                this.clientId = client.clientId
                this.clientSecret = client.clientSecret
                this.resourceIds = ""
                this.accessTokenValidity = client.tokenSettings.accessTokenTimeToLive.seconds.toInt()
                this.refreshTokenValidity = client.tokenSettings.refreshTokenTimeToLive.seconds.toInt()
                this.authorities = ""
                this.scope = StringUtils.collectionToCommaDelimitedString(client.scopes)
                this.autoapprove = ""
                this.enabled = true
                this.webServerRedirectUri = StringUtils.collectionToCommaDelimitedString(client.redirectUris)
                this.authorizedGrantTypes =
                    StringUtils.collectionToCommaDelimitedString(client.authorizationGrantTypes.map { it.value })
            }
            clientService.add(record)
        }
    }

    override fun findById(id: String?): RegisteredClient? {
        if (id.isNullOrBlank()) {
            return null
        }
        val r = clientService.getById(id)
        return r?.let {
            if (it.enabled == true) {
                RegisteredClient.withId(it.clientId)
                    .clientId(it.clientId)
                    .clientSecret(it.clientSecret)
                    .redirectUris { uris ->
                        uris.addAll(StringUtils.commaDelimitedListToStringArray(it.webServerRedirectUri.orEmpty()))
                    }
                    .authorizationGrantTypes { types ->
                        types.addAll(
                            StringUtils.commaDelimitedListToStringArray(it.authorizedGrantTypes.orEmpty())
                                .map { t -> AuthorizationGrantType(t) })
                    }
                    .scopes { ss ->
                        ss.addAll(StringUtils.commaDelimitedListToStringArray(it.scope.orEmpty()))
                    }
                    .tokenSettings(
                        TokenSettings
                            .builder()
                            .accessTokenTimeToLive(Duration.ofSeconds((it.accessTokenValidity ?: 3600).toLong()))
                            .refreshTokenTimeToLive(
                                Duration.ofSeconds(
                                    (it.refreshTokenValidity ?: (24 * 3600)).toLong()
                                )
                            )
                            .reuseRefreshTokens(oauth2ServerProperties?.token?.reuseRefreshToken ?: true)
                            .build()
                    )
                    .build()
            } else {
                null
            }
        }
    }

    override fun findByClientId(clientId: String?): RegisteredClient? {
        return this.findById(clientId)
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        oauth2ServerProperties = applicationContext.getBeansOfType(OAuth2ServerProperties::class.java).values.firstOrNull()
    }
}