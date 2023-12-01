package com.labijie.application.identity.service.impl

import com.labijie.application.configure
import com.labijie.application.exception.DataNotFoundException
import com.labijie.application.identity.IdentityCacheKeys
import com.labijie.application.identity.configuration.IdentityProperties
import com.labijie.application.identity.data.OAuth2ClientTable
import com.labijie.application.identity.data.pojo.OAuth2Client
import com.labijie.application.identity.data.pojo.dsl.OAuth2ClientDSL.insert
import com.labijie.application.identity.data.pojo.dsl.OAuth2ClientDSL.selectMany
import com.labijie.application.identity.data.pojo.dsl.OAuth2ClientDSL.update
import com.labijie.application.identity.service.IOAuth2ClientService
import com.labijie.application.removeAfterTransactionCommit
import com.labijie.caching.ICacheManager
import com.labijie.caching.getOrSet
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
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
) : IOAuth2ClientService {

    private var passwordEncoder: PasswordEncoder = NoOpPasswordEncoder.instance

    override fun setPasswordEncoder(passwordEncoder: PasswordEncoder) {
        this.passwordEncoder = passwordEncoder
    }

    override fun getById(clientId: String): OAuth2Client? {
        if (clientId.isBlank()) {
            return null
        }
        val clientDetails = this.getAll()

        return clientDetails.firstOrNull { it.clientId == clientId }
    }

    override fun getAll(): List<OAuth2Client> {
        return this.cache.getOrSet(
                IdentityCacheKeys.ALL_CLIENT_DETAILS,
                identityProperties.cacheRegion,
                identityProperties.cacheClientTimeout
        ) {

            transactionTemplate.configure(isReadOnly = true).execute {
                OAuth2ClientTable.selectMany {
                    orderBy(OAuth2ClientTable.clientId to SortOrder.ASC)
                }
            }
        } ?: listOf()
    }

    @Throws(IllegalArgumentException::class)
    override fun add(clientDetails: OAuth2Client) {
        this.transactionTemplate.execute {
            cache.removeAfterTransactionCommit(IdentityCacheKeys.ALL_CLIENT_DETAILS, identityProperties.cacheRegion)
            OAuth2ClientTable.insert(clientDetails)
        }
    }

    override fun remove(clientId: String) {
        if (clientId.isBlank()) {
            return
        }
        this.transactionTemplate.execute {
            cache.removeAfterTransactionCommit(IdentityCacheKeys.ALL_CLIENT_DETAILS, identityProperties.cacheRegion)
            OAuth2ClientTable.deleteWhere { OAuth2ClientTable.clientId eq clientId }
        }
    }

    override fun update(clientDetails: OAuth2Client) {

        val count = this.transactionTemplate.execute {
            cache.removeAfterTransactionCommit(IdentityCacheKeys.ALL_CLIENT_DETAILS, identityProperties.cacheRegion)
            OAuth2ClientTable.update(clientDetails) {
                OAuth2ClientTable.clientId eq clientDetails.clientId
            }
        } ?: 0
        if (count <= 0) {
            throw DataNotFoundException("Client with id ' ${clientDetails.clientId}' was not found")
        }
    }

    override fun updateSecret(clientId: String, secret: String) {
        if (clientId.isBlank() || secret.isBlank()) {
            throw IllegalArgumentException("Client id or secret to update can not be null.")
        }
        val updatingRecord = OAuth2Client().apply {
            this.clientId = clientId
            this.clientSecret = secret
        }


        val count = this.transactionTemplate.execute {
            cache.removeAfterTransactionCommit(IdentityCacheKeys.ALL_CLIENT_DETAILS, identityProperties.cacheRegion)
            OAuth2ClientTable.update(updatingRecord) {
                OAuth2ClientTable.clientId eq clientId
            }
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