package com.labijie.application.aot

import com.labijie.application.UrlProtocol
import com.labijie.application.configuration.ApplicationLocaleAutoConfiguration
import com.labijie.application.configuration.ApplicationWebAutoConfiguration
import com.labijie.application.model.CaptchaResponseData
import com.labijie.application.model.SmsSendRequest
import com.labijie.application.model.UserSmsSendRequest
import com.labijie.application.web.RestResponse
import com.labijie.application.web.annotation.HttpCache
import com.labijie.application.web.annotation.HumanVerify
import com.labijie.application.web.annotation.ResponseWrapped
import com.labijie.application.web.handler.ErrorResponse
import com.labijie.application.web.handler.InvalidParameterResponse
import com.labijie.application.web.handler.RequestParameterResponse
import org.springframework.aot.hint.MemberCategory
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
            ResponseWrapped::class
        )

        hints.resources().registerPattern("captcha/*.ttf")


        //spring doc
        hints.reflection().registerPackage(
            packageNames =  arrayOf(
                "org.springframework.security.oauth2.server.authorization.web",
                "org.springframework.security.oauth2.server.authorization.oidc.web"
            ), {
            it.getClassesImplementing(jakarta.servlet.Filter::class.java)
        }) {
            it.withMembers(*MemberCategory.entries.toTypedArray())
        }

        hints.reflection().registerPackage("org.springdoc.core.configuration.oauth2") {
            it.withMembers(*MemberCategory.entries.toTypedArray())
        }

        hints.reflection().registerType(ApplicationWebAutoConfiguration::class)
        hints.reflection().registerType(ApplicationLocaleAutoConfiguration::class)

        hints.reflection().registerForJackson(
            ErrorResponse::class,
            InvalidParameterResponse::class,
            RequestParameterResponse::class,
            RestResponse::class,
            UrlProtocol::class,
            CaptchaResponseData::class,
            SmsSendRequest::class,
            UserSmsSendRequest::class,
        )

    }
}