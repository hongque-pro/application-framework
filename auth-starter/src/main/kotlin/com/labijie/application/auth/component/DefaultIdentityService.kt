package com.labijie.application.auth.component

import com.labijie.application.ErrorCodedException
import com.labijie.application.auth.AuthErrors
import com.labijie.application.identity.data.pojo.User
import com.labijie.application.identity.service.IUserService
import com.labijie.application.web.roleAuthority
import com.labijie.infra.oauth2.*
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.core.userdetails.UsernameNotFoundException

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


    override fun getUserByName(userName: String): ITwoFactorUserDetails {
        val user = getUser(userName)
        val userLocked = (user.lockoutEnabled ?: false && (user.lockoutEnd ?: 0) > System.currentTimeMillis())

        if (userLocked) throw ErrorCodedException(error = AuthErrors.ACCOUNT_LOCKED, message = "account is locked!")

        val roles = ArrayList(
                userService.getUserRoles(user.id ?: 0).map {
                    roleAuthority(it.name.orEmpty())
                })

        return SimpleTwoFactorUserDetails(
                user.id.toString(),
                user.userName,
                credentialsNonExpired = true,
                enabled = true,
                password = user.passwordHash,
                accountNonExpired = true,
                accountNonLocked = !userLocked,
                twoFactorEnabled = false,
                authorities = roles
        )
    }


    private fun getUser(userName: String): User {
        return userService.getUser(userName)
                ?: throw UsernameNotFoundException("user with name '$userName' was not found.")
    }

}