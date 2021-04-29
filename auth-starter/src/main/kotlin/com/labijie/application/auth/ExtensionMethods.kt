package com.labijie.application.auth

import com.labijie.infra.oauth2.Constants
import org.springframework.security.oauth2.config.annotation.builders.ClientDetailsServiceBuilder
import java.time.Duration

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-12
 */
fun ClientDetailsServiceBuilder<*>.addClient(
    clientId: String,
    secret: String
): ClientDetailsServiceBuilder<*>.ClientBuilder {

    return this.withClient(clientId)
        .authorizedGrantTypes(
            Constants.GRANT_TYPE_PASSWORD,
            Constants.GRANT_TYPE_AUTHORIZATION_CODE,
            Constants.GRANT_TYPE_CLIENT_CREDENTIALS,
            Constants.GRANT_TYPE_IMPLICIT,
            Constants.GRANT_TYPE_REFRESH_TOKEN
        )
        .accessTokenValiditySeconds(Duration.ofMinutes(30).seconds.toInt())
        .refreshTokenValiditySeconds(Duration.ofDays(2).seconds.toInt())
        .autoApprove(true)
        .autoApprove("read", "write")
        .resourceIds()
        .secret(secret)
}