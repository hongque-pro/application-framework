package com.labijie.application.auth.component

import com.labijie.application.identity.data.pojo.OAuth2Client
import com.labijie.application.identity.service.IOAuth2ClientService
import com.labijie.infra.oauth2.configuration.OAuth2ServerProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings
import org.springframework.util.StringUtils
import java.time.Duration

/**
 *
 * @Author: Anders Xiao
 * @Date: 2021/12/13
 * @Description:
 */
class DefaultServerClientRepository(
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

            val record = OAuth2Client().apply {
                this.clientId = client.clientId
                this.clientSecret = client.clientSecret ?: ""
                this.resourceIds = ""
                this.accessTokenLiveSeconds = client.tokenSettings.accessTokenTimeToLive.toSeconds().toInt()
                this.refreshTokenLiveSeconds = client.tokenSettings.refreshTokenTimeToLive.toSeconds().toInt()
                this.authorizationCodeLiveSeconds = client.tokenSettings.authorizationCodeTimeToLive.toSeconds().toInt()
                this.deviceCodeLiveSeconds = client.tokenSettings.deviceCodeTimeToLive.toSeconds().toInt()
                this.reuseRefreshTokens = client.tokenSettings.isReuseRefreshTokens
                this.authorities = ""
                this.scopes = StringUtils.collectionToCommaDelimitedString(client.scopes)
                this.autoApprove = false
                this.enabled = true
                this.redirectUrls = StringUtils.collectionToCommaDelimitedString(client.redirectUris)
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
            if (it.enabled) {
                RegisteredClient.withId(it.clientId)
                    .clientId(it.clientId)
                    .clientSecret(it.clientSecret)
                    .redirectUris { uris ->
                        uris.addAll(StringUtils.commaDelimitedListToStringArray(it.redirectUrls.orEmpty()))
                    }
                    .authorizationGrantTypes { types ->
                        types.addAll(
                            StringUtils.commaDelimitedListToStringArray(it.authorizedGrantTypes.orEmpty())
                                .map { t -> AuthorizationGrantType(t) })
                    }
                    .scopes { ss ->
                        ss.addAll(StringUtils.commaDelimitedListToStringArray(it.scopes.orEmpty()))
                    }
                    .tokenSettings(
                        TokenSettings
                            .builder()
                            .accessTokenTimeToLive(Duration.ofSeconds(it.accessTokenLiveSeconds.toLong()))
                            .refreshTokenTimeToLive(
                                Duration.ofSeconds(it.refreshTokenLiveSeconds.toLong())
                            )
                            .authorizationCodeTimeToLive(Duration.ofSeconds(it.authorizationCodeLiveSeconds.toLong()))
                            .deviceCodeTimeToLive(Duration.ofSeconds(it.deviceCodeLiveSeconds.toLong()))
                            .reuseRefreshTokens(it.reuseRefreshTokens)
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