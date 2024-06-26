package com.labijie.application.auth.event

import com.labijie.application.auth.component.ISignInPlatformDetection
import com.labijie.application.identity.service.IUserService
import com.labijie.application.web.getRealIp
import com.labijie.infra.oauth2.events.UserSignedInEvent
import com.labijie.infra.utils.logger
import com.labijie.infra.utils.throwIfNecessary
import org.springframework.context.ApplicationListener
import org.springframework.scheduling.annotation.Async

open class UserSignInEventListener(
    private val signInPlatformDetection: ISignInPlatformDetection,
    private val userService: IUserService
) : ApplicationListener<UserSignedInEvent> {

    @Async
    override fun onApplicationEvent(event: UserSignedInEvent) {
        val userId = event.principle.userId.toLong()

        try {
            val request = event.httpServletRequest
            val ipAddress = request?.getRealIp() ?: "0.0.0.0"

            val platform = if (request == null) "web" else signInPlatformDetection.detect(request)

            userService.updateUserLastLogin(userId, ipAddress, platform)

        } catch (e: Exception) {
            logger.warn("Record user login properties fault.", e)
            e.throwIfNecessary()
        }
    }


}