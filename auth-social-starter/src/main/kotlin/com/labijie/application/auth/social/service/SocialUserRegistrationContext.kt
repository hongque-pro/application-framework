package com.labijie.application.auth.social.service

import com.labijie.application.auth.model.UserAndRoles
import com.labijie.application.auth.social.ILoginProvider
import com.labijie.application.auth.social.model.PlatformAccessToken
import com.labijie.application.auth.social.model.SocialRegisterInfo

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