package com.labijie.application.auth.social.service

import com.labijie.application.auth.configuration.AuthServerProperties
import com.labijie.application.auth.data.mapper.*
import com.labijie.application.auth.service.IUserService
import com.labijie.application.component.IMessageSender
import com.labijie.caching.ICacheManager
import com.labijie.infra.IIdGenerator
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.support.TransactionTemplate

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-11
 */
open class DefaultSocialUserService(
    authServerProperties: AuthServerProperties,
    idGenerator: IIdGenerator,
    messageSender: IMessageSender,
    cacheManager: ICacheManager,
    userMapper: UserMapper,
    userRoleMapper: UserRoleMapper,
    roleMapper: RoleMapper,
    userLoginMapper: UserLoginMapper,
    userOpenIdMapper: UserOpenIdMapper,
    transactionTemplate: TransactionTemplate
) : AbstractSocialUserService(
    authServerProperties,
    idGenerator,
    messageSender,
    cacheManager,
    userMapper,
    userRoleMapper,
    roleMapper,
    userLoginMapper,
    userOpenIdMapper,
    transactionTemplate
) {
    override fun getDefaultUserRoles(): Array<String> {
        return arrayOf("user")
    }
}