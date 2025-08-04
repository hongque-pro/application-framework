package com.labijie.application.aot

import com.labijie.application.ApplicationErrorRegistration
import com.labijie.application.ErrorDescription
import com.labijie.application.IDescribeEnum
import com.labijie.application.WellKnownClassNames
import com.labijie.application.annotation.ImportErrorDefinition
import com.labijie.application.component.impl.NoneHumanChecker
import com.labijie.application.jackson.DescribeEnumDeserializer
import com.labijie.application.jackson.DescribeEnumSerializer
import com.labijie.application.model.LocalizationMessages
import com.labijie.application.model.OneTimeCodeTarget
import com.labijie.application.model.SimpleValue
import com.labijie.application.thridparty.alipay.AlipayResponseBase
import com.labijie.application.thridparty.wechat.WechatResponse
import com.labijie.application.validation.*
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.aot.hint.TypeReference
import java.nio.channels.spi.SelectorProvider

class ApplicationCoreRuntimeHintsRegistrar : RuntimeHintsRegistrar {
    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {

        hints.registerGitInfo()


        hints.reflection().registerAnnotations(
            ImportErrorDefinition::class,
            ErrorDescription::class,
            ConfigurablePattern::class,
            DisplayName::class,
            StrongPassword::class,
            Username::class,
            XxsReject::class,
            PhoneNumber::class,
        )

        hints.reflection().registerEnum(OneTimeCodeTarget.Channel::class)

        hints.reflection().registerPackageForJackson(SimpleValue::class.java)

        hints.reflection().registerTypes(
            listOf(
                TypeReference.of(ConfigurablePatternValidator::class.java),
                TypeReference.of(DisplayNameAnnotationValidator::class.java),
                TypeReference.of(StrongPasswordAnnotationValidator::class.java),
                TypeReference.of(UsernameAnnotationValidator::class.java),
                TypeReference.of(XxsValidator::class.java)
            )
        ) {
            it.withMembers(MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS)
        }

        hints.reflection().registerForJackson(
            AlipayResponseBase::class,
            WechatResponse::class,
            LocalizationMessages::class
        )

        hints.reflection().registerType("org.springframework.web.context.WebApplicationContext")
        hints.reflection().registerType(WellKnownClassNames.TwoFactorPrincipal)
        hints.reflection().registerType(NoneHumanChecker::class.java)

        hints.reflection().registerTypes(
            listOf(
                TypeReference.of(ApplicationErrorRegistration::class.java),
            )
        ) {
            it.withMembers(MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_DECLARED_METHODS)
        }


        hints.reflection().registerTypes(
            listOf(
                TypeReference.of(DescribeEnumDeserializer::class.java),
                TypeReference.of(DescribeEnumSerializer::class.java),
                TypeReference.of(IDescribeEnum::class.java),
                TypeReference.of(SelectorProvider::class.java),
                TypeReference.of("org.slf4j.event.Level"),
                TypeReference.of(DescribeEnumDeserializer::class.java),
                TypeReference.of(DescribeEnumSerializer::class.java),
            )
        ) {
            it.withMembers(*MemberCategory.entries.toTypedArray())
        }

        hints.resources().apply {
            registerPattern("org/springframework/security/messages.properties")
            registerPattern("org/hibernate/validator/ValidationMessages.properties")
            registerPattern("com/labijie/application/messages.properties")

            // 如果有国际化版本，也要注册
            registerPattern("com/labijie/application/messages_*.properties")
        }

        hints.resources().registerPattern("antisamy-simple.xml")
        hints.resources().registerPattern("antisamy-strict.xml")
    }
}