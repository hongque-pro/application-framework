package com.labijie.application.aot

import com.labijie.application.UrlProtocol
import com.labijie.application.configuration.ApplicationLocaleAutoConfiguration
import com.labijie.application.configuration.ApplicationWebAutoConfiguration
import com.labijie.application.model.CaptchaResponseData
import com.labijie.application.model.OneTimeCodeVerifyRequest
import com.labijie.application.web.annotation.HttpCache
import com.labijie.application.web.annotation.HumanVerify
import com.labijie.application.web.annotation.OneTimeCodeVerify
import com.labijie.application.web.handler.ErrorResponse
import com.labijie.application.web.handler.InvalidParameterResponse
import com.labijie.application.web.handler.RequestParameterResponse
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.aot.hint.TypeReference


class ApplicationWebRuntimeHintsRegistrar : RuntimeHintsRegistrar {
    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        hints.reflection()
            .registerType(TypeReference.of("org.springframework.security.oauth2.server.authorization.OAuth2Authorization"))

        hints.reflection().registerAnnotations(
            HttpCache::class,
            HumanVerify::class,
            OneTimeCodeVerify::class
        )

        hints.resources().registerPattern("captcha/*.ttf")
        hints.reflection().registerForJackson(OneTimeCodeVerifyRequest::class)


        //spring doc
        hints.reflection().registerPackageForJackson(
            packageNames =  setOf(
                "org.springframework.security.oauth2.server.authorization.web",
                "org.springframework.security.oauth2.server.authorization.oidc.web"
            )) {
            it.getClassesImplementing(jakarta.servlet.Filter::class.java)
        }

        hints.reflection().registerPackageForJackson("org.springdoc.core.configuration.oauth2")

        hints.reflection().registerType(ApplicationWebAutoConfiguration::class)
        hints.reflection().registerType(ApplicationLocaleAutoConfiguration::class)

        hints.reflection().registerForJackson(
            ErrorResponse::class,
            InvalidParameterResponse::class,
            RequestParameterResponse::class,
            UrlProtocol::class,
            CaptchaResponseData::class,
            ErrorResponse::class,
        )

    }
}