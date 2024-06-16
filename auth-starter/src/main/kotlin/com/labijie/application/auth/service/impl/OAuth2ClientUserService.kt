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
import com.labijie.application.identity.data.pojo.dsl.UserLoginDSL.insert
import com.labijie.application.identity.data.pojo.dsl.UserLoginDSL.selectByPrimaryKey
import com.labijie.application.identity.exception.LoginProviderKeyAlreadyExistedException
import com.labijie.application.identity.model.UserAndRoles
import com.labijie.application.identity.service.IUserService
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.transaction.support.TransactionTemplate


class OAuth2ClientUserService(
    private val userService: IUserService,
    private val clientRegistrationRepository: ClientRegistrationRepository,
    private val transactionTemplate: TransactionTemplate) : IOAuth2ClientUserService {
    override fun getUserByOAuth2User(
        clientRegistrationId: String,
        oauth2UserId: String,
        includeRoles: Boolean
    ): UserAndRoles? {
        if (clientRegistrationId.isBlank() || oauth2UserId.isBlank()) {
            return null
        }
        return transactionTemplate.executeReadOnly {
           val login = UserLoginTable.selectByPrimaryKey(clientRegistrationId, oauth2UserId)
            login?.let {
                userService.getUserById(it.userId)?.let {
                    user->
                    val roles = if(includeRoles) {
                        userService.getUserRoles(userId = user.id)
                    }else {
                        listOf()
                    }
                    UserAndRoles(user, roles)
                }

            }
        }
    }


    override fun addOAuth2UserLoginToUser(userId: Long, oauth2UserToken: OAuth2UserToken) {
        transactionTemplate.execute {
            val client = clientRegistrationRepository.findByRegistrationId(oauth2UserToken.clientRegistrationId) ?: throw InvalidOAuth2ClientRegistrationException()
            val login = UserLoginTable.selectByPrimaryKey(oauth2UserToken.id, client.registrationId)
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