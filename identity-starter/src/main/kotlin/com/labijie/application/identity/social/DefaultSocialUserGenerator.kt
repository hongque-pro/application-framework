package com.labijie.application.identity.social

import com.labijie.application.identity.IdentityUtils
import com.labijie.infra.utils.ShortId
import com.labijie.infra.utils.ifNullOrBlank
import com.labijie.application.identity.data.UserRecord as User

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-11
 */
open class DefaultSocialUserGenerator : ISocialUserGenerator {
    override fun generate(context: UserGenerationContext, userType: Byte, userName:String?): User {
        val id = context.idGenerator.newId()
        val username = userName.ifNullOrBlank {  id.toString() }
        val pwd = context.passwordEncoder.encode(ShortId.newId())
        return IdentityUtils.createUser(id, username, context.phoneNumber, pwd, userType)
    }
}