package com.labijie.application.identity.service

import com.labijie.application.identity.model.SocialRegisterInfo
import com.labijie.application.identity.model.SocialUserAndRoles

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-11
 */
interface ISocialUserService : IUserService {
    fun getSocialUser(loginProvider: String, authorizationCode:String): SocialUserAndRoles?
    fun registerSocialUser(socialRegisterInfo: SocialRegisterInfo, throwIfExisted:Boolean = false, validateSms: Boolean = false): SocialUserAndRoles
    fun getOpenId(userId:Long, appId:String, loginProvider: String) : String?
}