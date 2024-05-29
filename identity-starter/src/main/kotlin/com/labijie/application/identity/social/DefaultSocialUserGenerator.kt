package com.labijie.application.identity.social

import com.labijie.application.identity.IdentityUtils
import com.labijie.application.identity.data.pojo.User
import com.labijie.infra.utils.ShortId
import com.labijie.infra.utils.ifNullOrBlank

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-11
 */
open class DefaultSocialUserGenerator : ISocialUserGenerator {
    override fun generate(
        context: UserGenerationContext,
        userType: Byte
    ): User {
        val id = context.idGenerator.newId()
        val username = context.username.ifNullOrBlank { "u${id}" }
        val u = IdentityUtils.createUser(id, username, userType)
        u.phoneNumber = context.phoneNumber
        u.email = context.email
        return u
    }
}