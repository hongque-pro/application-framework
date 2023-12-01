package com.labijie.application.auth

import com.labijie.application.identity.model.UserAndRoles
import com.labijie.application.web.roleAuthority
import com.labijie.infra.oauth2.ITwoFactorUserDetails
import com.labijie.infra.oauth2.OAuth2Utils
import com.labijie.infra.oauth2.SimpleTwoFactorUserDetails
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings
import org.springframework.util.CollectionUtils
import org.springframework.util.StringUtils
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

fun OAuth2AccessTokenAuthenticationToken.toHttpResponse(): Map<String, Any> {
    val parameters: MutableMap<String, Any> = HashMap()
    parameters[OAuth2ParameterNames.ACCESS_TOKEN] = this.accessToken.tokenValue
    parameters[OAuth2ParameterNames.TOKEN_TYPE] = this.accessToken.tokenType.value
    parameters[OAuth2ParameterNames.EXPIRES_IN] = this.accessToken.getExpiresInSeconds()
    if (!CollectionUtils.isEmpty(this.accessToken.scopes)) {
        parameters[OAuth2ParameterNames.SCOPE] = StringUtils.collectionToDelimitedString(this.accessToken.scopes, " ")
    }
    if (this.refreshToken != null) {
        parameters[OAuth2ParameterNames.REFRESH_TOKEN] = this.refreshToken!!.tokenValue
    }
    if (!CollectionUtils.isEmpty(this.additionalParameters)) {
        this.additionalParameters.forEach { (k, v) ->
            parameters[k] = v
        }
    }
    return parameters
}

fun OAuth2AccessToken.getExpiresInSeconds(): Long {
    return if (this.expiresAt != null) {
        ChronoUnit.SECONDS.between(Instant.now(), this.expiresAt)
    } else -1
}