/**
 * @author Anders Xiao
 * @date 2024-06-13
 */
package com.labijie.application.auth.service.impl

import com.labijie.application.auth.service.IOAuth2ClientUserService
import com.labijie.application.exception.UserNotFoundException
import com.labijie.application.identity.data.UserLoginTable
import com.labijie.application.identity.data.UserTable
import com.labijie.application.identity.data.pojo.UserLogin
import com.labijie.application.identity.data.pojo.dsl.UserDSL.selectByPrimaryKey
import com.labijie.application.identity.data.pojo.dsl.UserLoginDSL.deleteByPrimaryKey
import com.labijie.application.identity.data.pojo.dsl.UserLoginDSL.insert
import com.labijie.application.identity.data.pojo.dsl.UserLoginDSL.selectByPrimaryKey
import com.labijie.application.identity.exception.LoginProviderKeyAlreadyExistedException
import com.labijie.application.identity.model.UserAndRoles
import com.labijie.application.identity.service.IUserService
import com.labijie.infra.oauth2.client.StandardOidcUser
import org.slf4j.LoggerFactory
import org.springframework.transaction.support.TransactionTemplate


class DefaultOAuth2ClientUserService(
    private val userService: IUserService,
    private val transactionTemplate: TransactionTemplate) : IOAuth2ClientUserService {

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
                    logger.warn("Delete oauth2 user login (${provider}: ${oauth2UserId}), because user not found.")
                    UserLoginTable.deleteByPrimaryKey(provider, oauth2UserId)
                }
                r
            }
        }
    }


    override fun addOAuth2UserLoginToUser(userId: Long, oauth2UserToken: StandardOidcUser) {
        transactionTemplate.execute {
            UserTable.selectByPrimaryKey(userId, UserTable.id) ?: throw UserNotFoundException()
            val login = UserLoginTable.selectByPrimaryKey(oauth2UserToken.provider, oauth2UserToken.userId)
            if(login == null) {
                UserLogin().apply {
                    this.userId = userId
                    this.loginProvider = oauth2UserToken.provider
                    this.providerKey = oauth2UserToken.userId
                    this.providerDisplayName = oauth2UserToken.provider
                }.let {
                    UserLoginTable.insert(it)
                }
                return@execute
            }
            if(login.userId != userId) {
                throw LoginProviderKeyAlreadyExistedException(oauth2UserToken.provider)
            }
        }
    }

}