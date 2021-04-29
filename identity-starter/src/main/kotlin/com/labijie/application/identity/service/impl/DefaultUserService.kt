package com.labijie.application.identity.service.impl

import com.labijie.application.component.IMessageSender
import com.labijie.application.identity.configuration.IdentityProperties
import com.labijie.application.identity.data.mapper.*
import com.labijie.caching.ICacheManager
import com.labijie.infra.IIdGenerator
import org.springframework.transaction.support.TransactionTemplate

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-09
 */
open class DefaultUserService(
        identityProperties: IdentityProperties,
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
        identityProperties,
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