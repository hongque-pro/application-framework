package com.labijie.application.auth.configuration

import com.labijie.application.auth.doc.AuthServerOperationCustomizer
import com.labijie.application.auth.doc.AuthServerSecuritySchemeNames
import com.labijie.application.auth.doc.OAuth2ClientRequiredDocCustomizer
import com.labijie.application.auth.doc.ServerIdTokenParameterCustomizer
import com.labijie.application.auth.oauth2.OAuth2UserTokenArgumentResolver
import com.labijie.infra.oauth2.AccessToken
import com.labijie.infra.oauth2.OAuth2Constants
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.models.media.*
import org.springdoc.core.configuration.SpringDocConfiguration
import org.springdoc.core.configuration.oauth2.SpringDocOAuth2Token
import org.springdoc.core.utils.SpringDocUtils
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient

/**
 * @author Anders Xiao
 * @date 2023-12-02
 */
@ConditionalOnProperty(prefix = "springdoc.swagger-ui", name = ["enabled"], havingValue = "true", matchIfMissing = true)
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(SpringDocConfiguration::class)
@SecurityScheme(
    name = AuthServerSecuritySchemeNames.CLIENT_AUTHORIZATION,
    type = SecuritySchemeType.HTTP,
    scheme = "basic"
)
@SecurityScheme(
    name = AuthServerSecuritySchemeNames.SERVER_ID_TOKEN,
    type = SecuritySchemeType.APIKEY,
    paramName = OAuth2UserTokenArgumentResolver.ID_TOKEN_KEY
)
class AuthDocAutoConfiguration : InitializingBean {


    private val oauth2TokenSchema by lazy {
        MapSchema().apply {
            addProperty(OAuth2ParameterNames.ACCESS_TOKEN, StringSchema())
            addProperty(OAuth2ParameterNames.EXPIRES_IN, NumberSchema())
            addProperty(OAuth2ParameterNames.TOKEN_TYPE, StringSchema())
            addProperty(OAuth2ParameterNames.REFRESH_TOKEN, StringSchema().nullable(true))
            addProperty(OAuth2ParameterNames.SCOPE, StringSchema().nullable(true))
            addProperty(OAuth2Constants.CLAIM_TWO_FACTOR, BooleanSchema().nullable(true))
            addProperty(OAuth2Constants.CLAIM_USER_ID, StringSchema())
            addProperty(OAuth2Constants.CLAIM_USER_NAME, StringSchema())
            addProperty(OAuth2Constants.CLAIM_AUTHORITIES, ArraySchema().items(StringSchema()))
            addProperty(OAuth2Constants.CLAIM_AUD, StringSchema().nullable(true))
            addProperty(OAuth2Constants.CLAIM_ISS, StringSchema().nullable(true))
        }
    }

    override fun afterPropertiesSet() {
        SpringDocUtils.getConfig().apply {
            addRequestWrapperToIgnore(RegisteredClient::class.java)
            replaceWithSchema(AccessToken::class.java, oauth2TokenSchema)
            replaceWithSchema(SpringDocOAuth2Token::class.java, oauth2TokenSchema)
        }
    }

    @Bean
    fun oauth2UserDocParameterCustomizer(): ServerIdTokenParameterCustomizer {
        return ServerIdTokenParameterCustomizer()
    }

    @Bean
    fun oauth2ClientRequiredDocCustomizer(): OAuth2ClientRequiredDocCustomizer {
        return OAuth2ClientRequiredDocCustomizer()
    }

    @Bean
    fun authServerOperationCustomizer(authProperties: AuthProperties): AuthServerOperationCustomizer {
        return AuthServerOperationCustomizer(authProperties)
    }
}