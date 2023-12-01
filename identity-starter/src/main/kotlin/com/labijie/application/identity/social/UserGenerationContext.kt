package com.labijie.application.identity.social

import com.labijie.application.identity.model.PlatformAccessToken
import com.labijie.infra.IIdGenerator
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-11
 */
data class UserGenerationContext(
    val username: String?,
    val idGenerator: IIdGenerator,
    val loginProvider:String,
    val phoneNumber: String,
    val socialToken: PlatformAccessToken)