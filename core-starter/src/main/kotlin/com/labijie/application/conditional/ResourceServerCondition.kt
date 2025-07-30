package com.labijie.application.conditional

import com.labijie.application.WellKnownClassNames
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Condition
import org.springframework.context.annotation.ConditionContext
import org.springframework.core.type.AnnotatedTypeMetadata

/**
 * @author Anders Xiao
 * @date 2025/7/30
 */
class ResourceServerCondition : Condition {


    override fun matches(
        context: ConditionContext,
        metadata: AnnotatedTypeMetadata
    ): Boolean {

        val classLoader = context.classLoader

        ConditionalOnWebApplication()
        return try {
            Class.forName(WellKnownClassNames.TwoFactorPrincipal, false, classLoader)
            true
        } catch (_: ClassNotFoundException) {
            false
        }
    }

}