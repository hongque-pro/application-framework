package com.labijie.application.auth.service.impl

import com.labijie.application.auth.configuration.AuthServerProperties
import com.labijie.application.auth.data.mapper.RoleMapper
import com.labijie.application.auth.data.mapper.UserMapper
import com.labijie.application.auth.data.mapper.UserRoleMapper
import com.labijie.application.component.IMessageSender
import com.labijie.caching.ICacheManager
import com.labijie.infra.IIdGenerator
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.support.TransactionTemplate

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-09
 */
open class DefaultUserService(
    authServerProperties: AuthServerProperties,
    idGenerator: IIdGenerator,
    messageSender: IMessageSender,
    cacheManager: ICacheManager,
    userMapper: UserMapper,
    userRoleMapper: UserRoleMapper,
    roleMapper: RoleMapper,
    transactionTemplate: TransactionTemplate
) : AbstractUserService(
    authServerProperties,
    idGenerator,
    messageSender,
    cacheManager,
    userMapper,
    userRoleMapper,
    roleMapper,
    transactionTemplate
) {
    override fun getDefaultUserRoles(): Array<String> {
        return arrayOf("user")
    }
}