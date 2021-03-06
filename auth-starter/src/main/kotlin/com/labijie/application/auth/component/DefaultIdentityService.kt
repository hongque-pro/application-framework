package com.labijie.application.auth.component

import com.labijie.application.ErrorCodedException
import com.labijie.application.auth.AuthErrors.ACCOUNT_LOCKED
import com.labijie.application.auth.service.IUserService
import com.labijie.infra.oauth2.*
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.core.userdetails.UsernameNotFoundException
import com.labijie.application.auth.data.UserRecord as User

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-05
 */
open class DefaultIdentityService constructor(
    private val userService: IUserService,
    eventPublisher: ApplicationEventPublisher? = null
) : IIdentityService {
    private lateinit var applicationEventPublisher: ApplicationEventPublisher

    init {
        if (eventPublisher != null) {
            applicationEventPublisher = eventPublisher
        }
    }

    override fun authenticationChecks(authenticationCheckingContext: AuthenticationCheckingContext): SignInResult {

        return SignInResult(
            type = SignInResultType.Success,
            user = authenticationCheckingContext.userDetails
        )
    }

    override fun getUserByName(userName: String): ITwoFactorUserDetails {
        val user = getUser(userName)
        val userLocked = (user.lockoutEnabled ?: false && (user.lockoutEnd ?: 0) > System.currentTimeMillis())

        if (userLocked) throw ErrorCodedException(error = ACCOUNT_LOCKED, message = "account is locked!")

        val roles = userService.getUserRoles(user.id ?: 0)

        return SimpleTwoFactorUserDetails(
            user.id.toString(),
            user.userName.orEmpty(),
            credentialsNonExpired = true,
            enabled = true,
            password = user.passwordHash.orEmpty(),
            accountNonExpired = true,
            accountNonLocked = !userLocked,
            twoFactorEnabled = false,
            authorities = arrayListOf(*roles.map { GrantedAuthorityObject(it.name.orEmpty()) }.toTypedArray())
        )
    }


    private fun getUser(userName: String): User {
        return userService.getUser(userName)
            ?: throw UsernameNotFoundException("user with name '$userName' was not found.")
    }

}