package com.labijie.application.auth

import com.labijie.application.ErrorCodedException
import com.labijie.application.auth.oauth2.OAuth2UserTokenArgumentResolver
import com.labijie.application.identity.IdentityErrors
import com.labijie.application.identity.model.RegisterInfo
import com.labijie.application.identity.model.UserAndRoles
import com.labijie.application.web.roleAuthority
import com.labijie.infra.oauth2.ITwoFactorUserDetails
import com.labijie.infra.oauth2.OAuth2Utils
import com.labijie.infra.oauth2.SimpleTwoFactorUserDetails
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-12
 */
fun RegisteredClient.Builder.default(
    clientId: String,
    secret: String
): RegisteredClient.Builder {

    val tokenSettings = TokenSettings.builder()
        .accessTokenTimeToLive(Duration.ofHours(1))
        .refreshTokenTimeToLive(Duration.ofHours(24))
        .reuseRefreshTokens(true)
        .build()


    return this.clientId(clientId)
        .clientSecret(secret)
        .clientName(clientId)
        .authorizationGrantTypes {
            it.add(OAuth2Utils.PASSWORD_GRANT_TYPE)
            it.add(AuthorizationGrantType.AUTHORIZATION_CODE)
            it.add(AuthorizationGrantType.CLIENT_CREDENTIALS)
            it.add(AuthorizationGrantType.JWT_BEARER)
            it.add(AuthorizationGrantType.REFRESH_TOKEN)
        }
        .tokenSettings(tokenSettings)
}


fun <T : UserAndRoles> T.toPrincipal(configure: (T.() -> Map<String, String>)? = null): ITwoFactorUserDetails {
    val user = this.user
    val roles = this.roles.map {
        roleAuthority(it.name)
    }

    val enabled = !this.user.lockoutEnabled || this.user.lockoutEnd <= System.currentTimeMillis()

    return SimpleTwoFactorUserDetails(
        user.id.toString(),
        user.userName,
        true,
        enabled,
        user.passwordHash,
        true,
        !user.lockoutEnabled,
        user.twoFactorEnabled,
        ArrayList(
            roles
        ),
        if(configure != null) configure() else mapOf()
    )
}


fun OAuth2AccessToken.getExpiresInSeconds(): Long {
    return if (this.expiresAt != null) {
        ChronoUnit.SECONDS.between(Instant.now(), this.expiresAt)
    } else -1
}

fun UserAndRoles.toUserDetails() : ITwoFactorUserDetails {
    val userLocked = (user.lockoutEnabled && (user.lockoutEnd) > System.currentTimeMillis())

    if (userLocked) throw ErrorCodedException(error = IdentityErrors.ACCOUNT_LOCKED)

    val authorities = ArrayList(
        roles.map {
            roleAuthority(it.name)
        })

    return SimpleTwoFactorUserDetails(
        user.id.toString(),
        user.userName,
        credentialsNonExpired = true,
        enabled = true,
        password = user.passwordHash,
        accountNonExpired = true,
        accountNonLocked = true,
        twoFactorEnabled = false,
        authorities = authorities
    )
}

fun RegisterInfo.attachOAuth2User(auth2UserToken: String) {
    this.addition[OAuth2UserTokenArgumentResolver.TOKEN_PARAMETER_NAME] = auth2UserToken
}

fun RegisterInfo.getOAuth2User(): String? {
    return this.addition[OAuth2UserTokenArgumentResolver.TOKEN_PARAMETER_NAME]
}