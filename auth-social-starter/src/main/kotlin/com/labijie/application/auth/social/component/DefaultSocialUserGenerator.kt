package com.labijie.application.auth.social.component

import com.labijie.application.auth.AuthUtils
import com.labijie.application.auth.data.UserRecord as User
import com.labijie.application.toKebabCase
import com.labijie.infra.utils.ShortId

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
        return AuthUtils.createUser(id, userName, context.phoneNumber, pwd, userType)
    }
}