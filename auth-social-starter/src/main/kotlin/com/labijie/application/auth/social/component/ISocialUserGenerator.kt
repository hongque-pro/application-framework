package com.labijie.application.auth.social.component

import com.labijie.application.auth.data.UserRecord as User
/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-11
 */
interface ISocialUserGenerator {
    fun generate(context: UserGenerationContext, userType: Byte = 0):User
}