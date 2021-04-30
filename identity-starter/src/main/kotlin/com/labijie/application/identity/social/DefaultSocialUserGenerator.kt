package com.labijie.application.identity.social

import com.labijie.application.identity.IdentityUtils
import com.labijie.infra.utils.ShortId
import com.labijie.application.identity.data.UserRecord as User

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-11
 */
open class DefaultSocialUserGenerator : ISocialUserGenerator {
    override fun generate(context: UserGenerationContext, userType: Byte): User {
        val id = context.idGenerator.newId()
        val userName = id.toString()
        val pwd = context.passwordEncoder.encode(ShortId.newId())
        return IdentityUtils.createUser(id, userName, context.phoneNumber, pwd, userType)
    }
}