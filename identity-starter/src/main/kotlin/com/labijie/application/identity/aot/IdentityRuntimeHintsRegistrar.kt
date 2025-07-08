package com.labijie.application.identity.aot

import com.labijie.application.aot.registerForJackson
import com.labijie.application.identity.data.IdentityLongIdTable
import com.labijie.application.identity.data.IdentityTable
import com.labijie.application.identity.model.PasswordStrength
import com.labijie.application.identity.model.PlatformAccessToken
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.aot.hint.TypeReference


class IdentityRuntimeHintsRegistrar : RuntimeHintsRegistrar {
    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        hints.reflection().registerTypes(
            listOf(
                TypeReference.of(IdentityTable::class.java),
                TypeReference.of(IdentityLongIdTable::class.java)

            )
        ) {
            it.withMembers(
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.DECLARED_FIELDS,
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS
            )
        }

        hints.reflection().registerForJackson(
            PlatformAccessToken::class
        )

        hints.reflection().registerForJackson(PasswordStrength::class)
    }
}