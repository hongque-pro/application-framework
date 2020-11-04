package com.labijie.application.auth.social.component

import com.labijie.application.auth.social.model.PlatformAccessToken
import com.labijie.infra.IIdGenerator
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-11
 */
data class UserGenerationContext(
    val passwordEncoder: PasswordEncoder,
    val idGenerator: IIdGenerator,
    val loginProvider:String,
    val phoneNumber: String,
    val socialToken: PlatformAccessToken)