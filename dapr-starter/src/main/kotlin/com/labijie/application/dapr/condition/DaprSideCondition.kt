package com.labijie.application.dapr.condition

import org.springframework.boot.autoconfigure.condition.*
import org.springframework.context.annotation.ConditionContext
import org.springframework.core.annotation.AnnotationAttributes
import org.springframework.core.annotation.MergedAnnotation
import org.springframework.core.annotation.MergedAnnotationPredicates
import org.springframework.core.env.PropertyResolver
import org.springframework.core.type.AnnotatedTypeMetadata

/**
 * @author Anders Xiao
 * @date 2023-12-04
 */
class DaprSideCondition : SpringBootCondition() {
    override fun getMatchOutcome(context: ConditionContext, metadata: AnnotatedTypeMetadata): ConditionOutcome {
        val allAnnotationAttributes = metadata.annotations
            .stream<Annotation?>(ConditionalOnDaprPubsub::class.java.getName())
            .filter(MergedAnnotationPredicates.unique { obj: MergedAnnotation<Annotation?> -> obj.metaTypes })
            .map { obj: MergedAnnotation<Annotation?> -> obj.asAnnotationAttributes() }
            .toList()

        val noMatch = mutableListOf<ConditionMessage>()
        val match = mutableListOf<ConditionMessage>()

        for (annotationAttributes in allAnnotationAttributes) {
            val outcome = determineOutcome(annotationAttributes, context.environment)
            val list = if (outcome.isMatch) match else noMatch
            list.add(outcome.conditionMessage)
        }

        return if (noMatch.isNotEmpty()) {
            ConditionOutcome.noMatch(ConditionMessage.of(noMatch))
        } else ConditionOutcome.match(ConditionMessage.of(match))
    }


    private fun determineOutcome(
        annotationAttributes: AnnotationAttributes,
        resolver: PropertyResolver
    ): ConditionOutcome {
        val conditionSide = annotationAttributes["side"]?.toString()?.lowercase() ?: "pub"

        val pubEnabled = resolver.getProperty("application.dapr.pub-service-enabled")?.toBooleanStrictOrNull() ?: false
        val subEnabled = resolver.getProperty("application.dapr.sub-service-enabled")?.toBooleanStrictOrNull() ?: false

        return if (conditionSide == "sub" && !pubEnabled) {
            val message = ConditionMessage.forCondition(ConditionalOnDaprPubsub::class.java)
                .found("application.dapr.pub-service-enabled is false")
                .items()
            ConditionOutcome.noMatch(message)
        }
        else if(conditionSide == "sub" && !subEnabled) {
            val message = ConditionMessage.forCondition(ConditionalOnDaprPubsub::class.java)
                .found("application.dapr.sub-service-enabled is false")
                .items()
            ConditionOutcome.noMatch(message)
        }
        else {
            ConditionOutcome
                .match(ConditionMessage.forCondition(ConditionalOnProperty::class.java).because("matched"))
        }
    }

}