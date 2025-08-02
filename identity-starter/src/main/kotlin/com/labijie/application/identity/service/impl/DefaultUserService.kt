package com.labijie.application.identity.service.impl

import com.labijie.application.identity.component.ICustomUserDataPersistence
import com.labijie.application.identity.configuration.IdentityProperties
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
    identityProperties: IdentityProperties,
    idGenerator: IIdGenerator,
    passwordEncoder: PasswordEncoder,
    cacheManager: ICacheManager,
    transactionTemplate: TransactionTemplate,
    customUserPersistence: ICustomUserDataPersistence?
) : AbstractSocialUserService(
    identityProperties,
    passwordEncoder,
    idGenerator,
    cacheManager,
    transactionTemplate,
    customUserPersistence
) {
    override fun getDefaultUserRoles(): Set<String> {
        return setOf("user")
    }
}