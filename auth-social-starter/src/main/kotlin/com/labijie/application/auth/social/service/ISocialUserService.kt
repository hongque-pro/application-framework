package com.labijie.application.auth.social.service

import com.labijie.application.auth.model.UserAndRoles
import com.labijie.application.auth.service.IUserService
import com.labijie.application.auth.social.model.SocialRegisterInfo
import com.labijie.application.auth.social.model.SocialUserAndRoles

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-11
 */
interface ISocialUserService : IUserService {
    fun signInSocialUser(loginProvider: String, authorizationCode:String): SocialUserAndRoles?
    fun registerSocialUser(socialRegisterInfo: SocialRegisterInfo, throwIfExisted:Boolean = false): SocialUserAndRoles
    fun getOpenId(userId:Long, appId:String, loginProvider: String) : String?
}