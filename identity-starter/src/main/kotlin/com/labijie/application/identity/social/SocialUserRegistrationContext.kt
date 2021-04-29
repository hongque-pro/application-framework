package com.labijie.application.identity.social

import com.labijie.application.identity.model.PlatformAccessToken
import com.labijie.application.identity.model.SocialRegisterInfo
import com.labijie.application.identity.model.UserAndRoles

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-15
 */
class SocialUserRegistrationContext internal constructor(
        val registerInfo: SocialRegisterInfo,
        val user: UserAndRoles,
        val loginProvider: ILoginProvider,
        val platformAccessToken: PlatformAccessToken
)