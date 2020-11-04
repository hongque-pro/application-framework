package com.labijie.application.auth.component

import com.fasterxml.jackson.core.type.TypeReference
import com.labijie.application.auth.AuthCacheKeys
import com.labijie.application.auth.configuration.AuthServerProperties
import com.labijie.application.auth.data.OAuth2ClientDetailsRecord
import com.labijie.application.auth.data.extensions.*
import com.labijie.application.auth.data.mapper.OAuth2ClientDetailsDynamicSqlSupport.OAuth2ClientDetails
import com.labijie.application.auth.data.mapper.OAuth2ClientDetailsMapper
import com.labijie.application.removeAfterTransactionCommit
import com.labijie.caching.ICacheManager
import com.labijie.caching.getOrSet
import com.labijie.infra.json.JacksonHelper
import com.labijie.infra.utils.logger
import org.mybatis.dynamic.sql.SqlBuilder.isEqualTo
import org.mybatis.dynamic.sql.SqlTable
import org.mybatis.dynamic.sql.util.kotlin.mybatis3.deleteFrom
import org.mybatis.dynamic.sql.util.kotlin.mybatis3.selectList
import org.mybatis.dynamic.sql.util.kotlin.mybatis3.update
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.provider.ClientDetails
import org.springframework.security.oauth2.provider.ClientDetailsService
import org.springframework.security.oauth2.provider.ClientRegistrationService
import org.springframework.security.oauth2.provider.NoSuchClientException
import org.springframework.security.oauth2.provider.client.BaseClientDetails
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils
import java.util.concurrent.TimeUnit

/**
 *
 * @author lishiwen
 * @date 19-12-6
 * @since JDK1.8
 */
open class MybatisClientDetailsService(
    private val authServerProperties: AuthServerProperties,
    protected val cache: ICacheManager,
    protected val clientMapper: OAuth2ClientDetailsMapper
) : ClientDetailsService, ClientRegistrationService {

    private var passwordEncoder: PasswordEncoder = NoOpPasswordEncoder.instance

    fun setPasswordEncoder(passwordEncoder: PasswordEncoder) {
        this.passwordEncoder = passwordEncoder
    }

    @Transactional(readOnly = true)
    override fun loadClientByClientId(clientId: String?): ClientDetails? {
        val clientDetails = this.listClientDetails()

        return clientDetails.firstOrNull { it.clientId == clientId }
    }

    override fun listClientDetails(): MutableList<out ClientDetails> {
        val data = this.cache.getOrSet(
            AuthCacheKeys.ALL_CLIENT_DETAILS,
            authServerProperties.cacheRegion,
            authServerProperties.cacheClientTimeout
        ) {

            clientMapper.select {
                orderBy(OAuth2ClientDetails.clientId)
            }
        }
        return data?.map(this::model2ClientDetails)?.toMutableList() ?: mutableListOf()
    }

    @Transactional
    @Throws(IllegalArgumentException::class)
    override fun addClientDetails(clientDetails: ClientDetails?) {
        val d = clientDetails ?: throw IllegalArgumentException("Client details to add can not be null.")
        val record = clientDetails2Model(d)
        cache.removeAfterTransactionCommit(AuthCacheKeys.ALL_CLIENT_DETAILS, authServerProperties.cacheRegion)
        clientMapper.insertSelective(record)
    }

    @Transactional
    override fun removeClientDetails(clientId: String?) {
        if (clientId.isNullOrBlank()) {
            return
        }
        cache.removeAfterTransactionCommit(AuthCacheKeys.ALL_CLIENT_DETAILS, authServerProperties.cacheRegion)
        clientMapper.deleteByPrimaryKey(clientId)
    }

    @Transactional
    override fun updateClientDetails(clientDetails: ClientDetails?) {
        val d = clientDetails ?: throw IllegalArgumentException("Client details to add can not be null.")
        val record = clientDetails2Model(d)

        cache.removeAfterTransactionCommit(AuthCacheKeys.ALL_CLIENT_DETAILS, authServerProperties.cacheRegion)
        val count = clientMapper.updateByPrimaryKeySelective(record)
        if (count <= 0) {
            throw NoSuchClientException("Client with id ${d.clientId} was not found")
        }
    }

    @Transactional
    override fun updateClientSecret(clientId: String?, secret: String?) {
        if (clientId.isNullOrBlank() || secret.isNullOrBlank()) {
            throw IllegalArgumentException("Client id or secret to update can not be null.")
        }
        val updatingRecord = OAuth2ClientDetailsRecord().apply {
            this.clientId = clientId
            this.clientSecret = secret
        }

        cache.removeAfterTransactionCommit(AuthCacheKeys.ALL_CLIENT_DETAILS, authServerProperties.cacheRegion)
        if (clientMapper.updateByPrimaryKeySelective(updatingRecord) <= 0) {
            throw NoSuchClientException("Client with id ${clientId} was not found")
        }
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

    companion object {
        class NoOpPasswordEncoder private constructor() : PasswordEncoder {

            override fun encode(rawPassword: CharSequence): String {
                return rawPassword.toString()
            }

            override fun matches(rawPassword: CharSequence, encodedPassword: String): Boolean {
                return rawPassword.toString() == encodedPassword
            }

            companion object {
                val instance: PasswordEncoder = NoOpPasswordEncoder()
            }

        }
    }
}