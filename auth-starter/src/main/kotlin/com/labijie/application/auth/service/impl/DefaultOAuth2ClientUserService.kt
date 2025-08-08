/**
 * @author Anders Xiao
 * @date 2024-06-13
 */
package com.labijie.application.auth.service.impl

import com.labijie.application.auth.AuthErrors
import com.labijie.application.auth.service.IOAuth2ClientUserService
import com.labijie.application.exception.UserNotFoundException
import com.labijie.application.identity.data.UserLoginTable
import com.labijie.application.identity.data.UserTable
import com.labijie.application.identity.data.pojo.User
import com.labijie.application.identity.data.pojo.UserLogin
import com.labijie.application.identity.data.pojo.dsl.UserDSL.selectByPrimaryKey
import com.labijie.application.identity.data.pojo.dsl.UserLoginDSL.deleteByPrimaryKey
import com.labijie.application.identity.data.pojo.dsl.UserLoginDSL.insert
import com.labijie.application.identity.data.pojo.dsl.UserLoginDSL.selectByPrimaryKey
import com.labijie.application.identity.data.pojo.dsl.UserLoginDSL.selectOne
import com.labijie.application.identity.model.RegisterBy
import com.labijie.application.identity.model.RegisterInfo
import com.labijie.application.identity.model.UserAndRoles
import com.labijie.application.identity.service.IUserService
import com.labijie.application.localeErrorMessage
import com.labijie.infra.oauth2.StandardOidcUser
import com.labijie.infra.oauth2.client.exception.OAuth2AccountLinkedAnotherUserException
import com.labijie.infra.oauth2.client.exception.OAuth2LoginException
import com.labijie.infra.oauth2.client.exception.OAuth2ProviderAlreadyLinkedException
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.andWhere
import org.slf4j.LoggerFactory
import org.springframework.transaction.support.TransactionTemplate


class DefaultOAuth2ClientUserService(
    private val userService: IUserService,
    private val transactionTemplate: TransactionTemplate
) : IOAuth2ClientUserService {

    companion object {
        private val logger by lazy {
            LoggerFactory.getLogger(DefaultOAuth2ClientUserService::class.java)
        }
    }

    override fun getUserByOAuth2User(
        provider: String,
        oauth2UserId: String,
        includeRoles: Boolean
    ): UserAndRoles? {
        if (provider.isBlank() || oauth2UserId.isBlank()) {
            return null
        }
        return transactionTemplate.execute {
            val login = UserLoginTable.selectByPrimaryKey(provider, oauth2UserId)
            return@execute login?.let {
                val r = userService.getUserById(it.userId)?.let { user ->
                    val roles = if (includeRoles) {
                        userService.getUserRoles(userId = user.id)
                    } else {
                        listOf()
                    }
                    UserAndRoles(user, roles)
                }
                if (r == null) {
                    //这里如果不删除，可能造成无法创建第三方账号，因为主键冲突，这条数据是僵尸数据。
                    logger.warn("Delete oauth2 user login (${provider}: ${oauth2UserId}), because user not found.")
                    UserLoginTable.deleteByPrimaryKey(provider, oauth2UserId)
                }
                r
            }
        }
    }


    private fun bindUserToOAuth2User(user: User, oauth2User: StandardOidcUser) {
        val userId = user.id
        this.transactionTemplate.execute {
            val login = UserLoginTable.selectByPrimaryKey(oauth2User.provider, oauth2User.userId)
            if (login == null) {
                UserLogin().apply {
                    this.userId = userId
                    this.loginProvider = oauth2User.provider
                    this.providerKey = oauth2User.userId
                    this.providerDisplayName = oauth2User.provider
                }.let {
                    UserLoginTable.insert(it)
                }
                return@execute
            }
            if (login.userId != userId) {
                throw OAuth2AccountLinkedAnotherUserException(oauth2User.provider)
            } else if (login.providerKey != oauth2User.userId) {
                throw OAuth2ProviderAlreadyLinkedException(oauth2User.provider)
            }
        }
    }

    override fun addUserLoginToUser(user: String, oauth2UserToken: StandardOidcUser) {
        if (user.isBlank()) {
            throw UserNotFoundException(user)
        }
        this.transactionTemplate.execute {
            val user = userService.getUser(user) ?: throw UserNotFoundException(user)
            bindUserToOAuth2User(user, oauth2UserToken)
        }
    }

    override fun addUserLoginToUser(userId: Long, oauth2UserToken: StandardOidcUser) {
        if (userId <= 0) throw UserNotFoundException()
        transactionTemplate.execute {
            val user = UserTable.selectByPrimaryKey(userId, UserTable.id) ?: throw UserNotFoundException()
            bindUserToOAuth2User(user, oauth2UserToken)
        }
    }

    override fun registerOAuth2User(
        user: StandardOidcUser,
        registerInfo: RegisterInfo,
        forceBy: RegisterBy?
    ): UserAndRoles {
        return this.transactionTemplate.execute {
            val login = UserLoginTable.selectByPrimaryKey(user.provider, user.userId)
            if (login != null) {
                throw OAuth2LoginException(
                    user.provider, AuthErrors.OAUTH2_PROVIDER_ALREADY_LINKED,
                    localeErrorMessage(AuthErrors.OAUTH2_PROVIDER_ALREADY_LINKED)
                )
            }

            val u = userService.registerUser(registerInfo, forceBy)
            val userLogin = UserLogin().apply {
                this.loginProvider = user.provider
                this.userId = u.user.id
                this.providerKey = user.userId
                this.providerDisplayName = user.provider
            }
            UserLoginTable.insert(userLogin)
            u
        }!!
    }

    override fun deleteUserLoginToUser(userId: Long, provider: String): UserLogin? {
        return this.transactionTemplate.execute {
            UserLoginTable.selectOne {
                andWhere {
                    UserLoginTable.userId.eq(userId) and
                    UserLoginTable.loginProvider.eq(provider)
                }
            }?.apply {
                UserLoginTable.deleteByPrimaryKey(this.loginProvider, this.providerKey)
            }
        }
    }
}