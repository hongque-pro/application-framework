package com.labijie.application.identity.social

import com.labijie.application.identity.IdentityUtils
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
    ): com.labijie.application.identity.data.UserRecord {
        val id = context.idGenerator.newId()
        val username = context.username.ifNullOrBlank { "u${id}" }
        val passwordHash = (context.plainPassword ?: ShortId.newId()).let { context.passwordEncoder.encode(it) }
        return IdentityUtils.createUser(id, username, context.phoneNumber, passwordHash, userType)
    }
}