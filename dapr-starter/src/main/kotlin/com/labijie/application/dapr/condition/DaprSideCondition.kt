package com.labijie.application.dapr.condition

import com.labijie.application.dapr.PubsubSide
import org.springframework.boot.autoconfigure.condition.ConditionMessage
import org.springframework.boot.autoconfigure.condition.ConditionOutcome
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.SpringBootCondition
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
        val conditionSide = annotationAttributes["side"] as? PubsubSide ?: PubsubSide.None

        val pubEnabled = resolver.getProperty("application.dapr.pub-service-enabled")?.toBooleanStrictOrNull() ?: false
        val subEnabled = resolver.getProperty("application.dapr.sub-service-enabled")?.toBooleanStrictOrNull() ?: false

        return if (conditionSide == PubsubSide.None && (pubEnabled || subEnabled)) {
            val message = ConditionMessage.forCondition(ConditionalOnDaprPubsub::class.java)
                .found(
                    "application.dapr.pub-service-enabled is $pubEnabled" + System.lineSeparator() +
                            "application.dapr.sub-service-enabled is $subEnabled" + System.lineSeparator() +
                            "side condition is ${PubsubSide.None}"
                )
                .items()
            ConditionOutcome.noMatch(message)
        } else if (conditionSide == PubsubSide.Pub && !pubEnabled) {
            val message = ConditionMessage.forCondition(ConditionalOnDaprPubsub::class.java)
                .found("application.dapr.pub-service-enabled is false")
                .items()
            ConditionOutcome.noMatch(message)
        } else if (conditionSide == PubsubSide.Sub && !subEnabled) {
            val message = ConditionMessage.forCondition(ConditionalOnDaprPubsub::class.java)
                .found("application.dapr.sub-service-enabled is false")
                .items()
            ConditionOutcome.noMatch(message)
        } else {
            ConditionOutcome
                .match(ConditionMessage.forCondition(ConditionalOnProperty::class.java).because("matched"))
        }
    }

}