/**
 * @author Anders Xiao
 * @date 2024-06-13
 */
package com.labijie.application.auth.service.impl

import com.labijie.application.auth.exception.InvalidOAuth2ClientRegistrationException
import com.labijie.application.auth.oauth2.OAuth2UserToken
import com.labijie.application.auth.service.IOAuth2ClientUserService
import com.labijie.application.executeReadOnly
import com.labijie.application.identity.data.UserLoginTable
import com.labijie.application.identity.data.pojo.UserLogin
import com.labijie.application.identity.data.pojo.dsl.UserLoginDSL.deleteByPrimaryKey
import com.labijie.application.identity.data.pojo.dsl.UserLoginDSL.insert
import com.labijie.application.identity.data.pojo.dsl.UserLoginDSL.selectByPrimaryKey
import com.labijie.application.identity.exception.LoginProviderKeyAlreadyExistedException
import com.labijie.application.identity.model.UserAndRoles
import com.labijie.application.identity.service.IUserService
import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.transaction.support.TransactionTemplate


class OAuth2ClientUserService(
    private val userService: IUserService,
    private val clientRegistrationRepository: ClientRegistrationRepository,
    private val transactionTemplate: TransactionTemplate) : IOAuth2ClientUserService {

    companion object {
        private val logger by lazy {
            LoggerFactory.getLogger(OAuth2ClientUserService::class.java)
        }
    }

    override fun getUserByOAuth2User(
        clientRegistrationId: String,
        oauth2UserId: String,
        includeRoles: Boolean
    ): UserAndRoles? {
        if (clientRegistrationId.isBlank() || oauth2UserId.isBlank()) {
            return null
        }
        return transactionTemplate.execute {
           val login = UserLoginTable.selectByPrimaryKey(clientRegistrationId, oauth2UserId)
            return@execute login?.let {
                val r = userService.getUserById(it.userId)?.let {
                    user->
                    val roles = if(includeRoles) {
                        userService.getUserRoles(userId = user.id)
                    }else {
                        listOf()
                    }
                    UserAndRoles(user, roles)
                }
                if(r == null) {
                    //这里如果不删除，可能造成无法创建第三方账号，因为主键冲突，这条数据是僵尸数据。
                    logger.warn("Delete oauth2 user login (${clientRegistrationId}: ${oauth2UserId}), because user not found.")
                    UserLoginTable.deleteByPrimaryKey(clientRegistrationId, oauth2UserId)
                }
                r
            }
        }
    }


    override fun addOAuth2UserLoginToUser(userId: Long, oauth2UserToken: OAuth2UserToken) {
        transactionTemplate.execute {
            val client = clientRegistrationRepository.findByRegistrationId(oauth2UserToken.clientRegistrationId) ?: throw InvalidOAuth2ClientRegistrationException()
            val login = UserLoginTable.selectByPrimaryKey(client.registrationId, oauth2UserToken.id)
            if(login == null) {
                UserLogin().apply {
                    this.userId = userId
                    this.loginProvider = client.registrationId
                    this.providerKey = oauth2UserToken.id
                    this.providerDisplayName = client.clientName
                }.let {
                    UserLoginTable.insert(it)
                }
                return@execute
            }
            if(login.userId != userId) {
                throw LoginProviderKeyAlreadyExistedException(client.clientName)
            }
        }
    }

}