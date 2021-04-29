package com.labijie.application.identity.social

import com.labijie.application.identity.data.UserRecord as User
/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-11
 */
interface ISocialUserGenerator {
    fun generate(context: UserGenerationContext, userType: Byte = 0):User
}