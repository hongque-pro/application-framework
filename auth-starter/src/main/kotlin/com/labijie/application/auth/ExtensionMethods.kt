package com.labijie.application.auth

import com.labijie.application.identity.data.UserRecord
import com.labijie.application.identity.data.mapper.UserDynamicSqlSupport
import com.labijie.application.identity.model.UserAndRoles
import com.labijie.infra.oauth2.ITwoFactorUserDetails
import com.labijie.infra.oauth2.SimpleTwoFactorUserDetails
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.config.TokenSettings
import java.time.Duration

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
            it.add(AuthorizationGrantType.PASSWORD)
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
        SimpleGrantedAuthority(it.name)
    }
    return SimpleTwoFactorUserDetails(
        user.id!!.toString(),
        user.userName?.toString().orEmpty(),
        true,
        true,
        user.passwordHash.orEmpty(),
        true,
        !(user.lockoutEnabled ?: false),
        user.twoFactorEnabled ?: false,
        ArrayList(
            roles
        ),
        if(configure != null) configure() else mapOf()
    )
}