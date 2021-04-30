package com.labijie.application.auth.component

import com.fasterxml.jackson.core.type.TypeReference
import com.labijie.application.identity.data.OAuth2ClientDetailsRecord
import com.labijie.application.identity.service.IOAuth2ClientService
import com.labijie.infra.json.JacksonHelper
import com.labijie.infra.utils.logger
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.provider.ClientDetails
import org.springframework.security.oauth2.provider.ClientDetailsService
import org.springframework.security.oauth2.provider.ClientRegistrationService
import org.springframework.security.oauth2.provider.client.BaseClientDetails
import org.springframework.util.StringUtils
import java.util.concurrent.TimeUnit

/**
 *
 * @author lishiwen
 * @date 19-12-6
 * @since JDK1.8
 */
open class AuthClientDetailsService(
    private val oAuth2ClientService: IOAuth2ClientService
) : ClientDetailsService, ClientRegistrationService {

    fun setPasswordEncoder(passwordEncoder: PasswordEncoder) {
         this.oAuth2ClientService.setPasswordEncoder(passwordEncoder)
    }

    override fun loadClientByClientId(clientId: String?): ClientDetails? {
        return this.oAuth2ClientService.getById(clientId.orEmpty())?.run {
            model2ClientDetails(this)
        }
    }

    override fun listClientDetails(): MutableList<out ClientDetails> {
        return this.oAuth2ClientService.getAll().map {
            model2ClientDetails(it)
        } as MutableList<out ClientDetails>
    }

    override fun addClientDetails(clientDetails: ClientDetails?) {
        val d = clientDetails ?: throw IllegalArgumentException("Client details to add can not be null.")
        val record = clientDetails2Model(d)
        this.oAuth2ClientService.add(record)
    }

    override fun removeClientDetails(clientId: String?) {
        this.oAuth2ClientService.remove(clientId.orEmpty())
    }

    override fun updateClientDetails(clientDetails: ClientDetails?) {
        val d = clientDetails ?: throw IllegalArgumentException("Client details to add can not be null.")
        val record = clientDetails2Model(d)

        this.oAuth2ClientService.update(record)
    }

    override fun updateClientSecret(clientId: String?, secret: String?) {
        if (clientId.isNullOrBlank() || secret.isNullOrBlank()) {
            throw IllegalArgumentException("Client id or secret to update can not be null.")
        }

        this.oAuth2ClientService.updateSecret(clientId, secret)
    }


    protected fun clientDetails2Model(client: ClientDetails): OAuth2ClientDetailsRecord {

        return OAuth2ClientDetailsRecord().apply {
            this.clientId = client.clientId
            this.resourceIds = StringUtils.collectionToCommaDelimitedString(client.resourceIds)
            this.scope = StringUtils.collectionToCommaDelimitedString(client.scope)
            this.authorizedGrantTypes = StringUtils.collectionToCommaDelimitedString(client.authorizedGrantTypes)
            this.authorities = StringUtils.collectionToCommaDelimitedString(client.authorities.map { it.authority })
            this.webServerRedirectUri = StringUtils.collectionToCommaDelimitedString(client.registeredRedirectUri)
            this.accessTokenValidity = client.accessTokenValiditySeconds ?: TimeUnit.MINUTES.toSeconds(30).toInt()
            this.accessTokenValidity = client.refreshTokenValiditySeconds ?: TimeUnit.DAYS.toSeconds(2).toInt()
            this.additionalInformation = if (client.additionalInformation?.isEmpty() ?: true) {
                ""
            } else JacksonHelper.serializeAsString(client.additionalInformation)
        }
    }

    protected fun model2ClientDetails(model: OAuth2ClientDetailsRecord): ClientDetails {
        return BaseClientDetails(
            model.clientId,
            model.resourceIds,
            model.scope,
            model.authorizedGrantTypes,
            model.authorities,
            model.webServerRedirectUri
        )
            .apply {
                this.clientSecret = model.clientSecret
                if ((model.accessTokenValidity ?: 0) > 0) {
                    this.accessTokenValiditySeconds = model.accessTokenValidity
                }
                if ((model.refreshTokenValidity ?: 0) > 0) {
                    this.refreshTokenValiditySeconds = model.refreshTokenValidity
                }
                if (!model.scope.isNullOrBlank()) {
                    this.setAutoApproveScopes(StringUtils.commaDelimitedListToSet(model.scope))
                }
                val json = model.additionalInformation
                if (!json.isNullOrBlank()) {
                    val typeReference = object : TypeReference<Map<String, *>>() {}
                    try {
                        val adds = JacksonHelper.deserializeFromString(json, typeReference)
                        this.additionalInformation = adds
                    } catch (t: Throwable) {
                        logger.error("Could not decode JSON for additional information: ${json}", t)
                    }
                }
            }
    }
}