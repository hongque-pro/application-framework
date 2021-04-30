package com.labijie.application.identity.service.impl

import com.labijie.application.configure
import com.labijie.application.exception.DataNotFoundException
import com.labijie.application.identity.IdentityCacheKeys
import com.labijie.application.identity.configuration.IdentityProperties
import com.labijie.application.identity.data.OAuth2ClientDetailsRecord
import com.labijie.application.identity.data.extensions.deleteByPrimaryKey
import com.labijie.application.identity.data.extensions.insertSelective
import com.labijie.application.identity.data.extensions.select
import com.labijie.application.identity.data.extensions.updateByPrimaryKeySelective
import com.labijie.application.identity.data.mapper.OAuth2ClientDetailsDynamicSqlSupport.OAuth2ClientDetails
import com.labijie.application.identity.data.mapper.OAuth2ClientDetailsMapper
import com.labijie.application.identity.service.IOAuth2ClientService
import com.labijie.application.removeAfterTransactionCommit
import com.labijie.caching.ICacheManager
import com.labijie.caching.getOrSet
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.support.TransactionTemplate


/**
 *
 * @author lishiwen
 * @date 19-12-6
 * @since JDK1.8
 */
open class DefaultOAuth2ClientService(
        private val transactionTemplate: TransactionTemplate,
        private val identityProperties: IdentityProperties,
        protected val cache: ICacheManager,
        protected val clientMapper: OAuth2ClientDetailsMapper
) : IOAuth2ClientService {

    private var passwordEncoder: PasswordEncoder = NoOpPasswordEncoder.instance

    override fun setPasswordEncoder(passwordEncoder: PasswordEncoder) {
        this.passwordEncoder = passwordEncoder
    }

    override fun getById(clientId: String): OAuth2ClientDetailsRecord? {
        if (clientId.isBlank()) {
            return null
        }
        val clientDetails = this.getAll()

        return clientDetails.firstOrNull { it.clientId == clientId }
    }

    override fun getAll(): List<OAuth2ClientDetailsRecord> {
        return this.cache.getOrSet(
                IdentityCacheKeys.ALL_CLIENT_DETAILS,
                identityProperties.cacheRegion,
                identityProperties.cacheClientTimeout
        ) {

            transactionTemplate.configure(isReadOnly = true).execute {
                clientMapper.select {
                    orderBy(OAuth2ClientDetails.clientId)
                }
            }
        } ?: listOf()
    }

    @Throws(IllegalArgumentException::class)
    override fun add(clientDetails: OAuth2ClientDetailsRecord) {
        cache.removeAfterTransactionCommit(IdentityCacheKeys.ALL_CLIENT_DETAILS, identityProperties.cacheRegion)
        this.transactionTemplate.execute {
            clientMapper.insertSelective(clientDetails)
        }
    }

    override fun remove(clientId: String) {
        if (clientId.isBlank()) {
            return
        }
        cache.removeAfterTransactionCommit(IdentityCacheKeys.ALL_CLIENT_DETAILS, identityProperties.cacheRegion)
        this.transactionTemplate.execute {
            clientMapper.deleteByPrimaryKey(clientId)
        }
    }

    override fun update(clientDetails: OAuth2ClientDetailsRecord) {

        cache.removeAfterTransactionCommit(IdentityCacheKeys.ALL_CLIENT_DETAILS, identityProperties.cacheRegion)
        val count = this.transactionTemplate.execute {
            clientMapper.updateByPrimaryKeySelective(clientDetails)
        } ?: 0
        if (count <= 0) {
            throw DataNotFoundException("Client with id ' ${clientDetails.clientId}' was not found")
        }
    }

    override fun updateSecret(clientId: String, secret: String) {
        if (clientId.isBlank() || secret.isBlank()) {
            throw IllegalArgumentException("Client id or secret to update can not be null.")
        }
        val updatingRecord = OAuth2ClientDetailsRecord().apply {
            this.clientId = clientId
            this.clientSecret = secret
        }

        cache.removeAfterTransactionCommit(IdentityCacheKeys.ALL_CLIENT_DETAILS, identityProperties.cacheRegion)

        val count = this.transactionTemplate.execute {
            clientMapper.updateByPrimaryKeySelective(updatingRecord)
        } ?: 0

        if (count <= 0) {
            throw DataNotFoundException("Client with id '$clientId' was not found")
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