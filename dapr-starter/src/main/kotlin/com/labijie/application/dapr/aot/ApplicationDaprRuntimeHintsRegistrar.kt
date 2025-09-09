package com.labijie.application.dapr.aot

import com.fasterxml.jackson.datatype.jsr310.deser.OffsetTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer
import com.labijie.application.aot.registerAnnotations
import com.labijie.application.aot.registerForJackson
import com.labijie.application.aot.registerType
import com.labijie.application.dapr.DaprClusterEvent
import com.labijie.application.dapr.annotation.EnableDaprClusterEventListener
import com.labijie.application.dapr.model.DaprSms
import io.dapr.client.domain.CloudEvent
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.aot.hint.TypeReference


class ApplicationDaprRuntimeHintsRegistrar : RuntimeHintsRegistrar {
    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {

        hints.reflection().registerType("org.springdoc.core.models.GroupedOpenApi")
        hints.reflection().registerAnnotations(EnableDaprClusterEventListener::class)

        hints.reflection().registerForJackson(
            DaprSms::class,
            DaprClusterEvent::class,
            CloudEvent::class,
            OffsetDateTimeSerializer::class,
            OffsetTimeDeserializer::class
        )


        //RuntimeClassInitialization.initializeAtRunTime("io.netty.handler.ssl.JdkSslServerContext", "io.netty.handler.ssl.BouncyCastleAlpnSslUtils")

        hints.reflection().registerTypes(
            listOf(
                TypeReference.of("io.grpc.netty.shaded.io.netty.util.internal.shaded.org.jctools.queues.BaseMpscLinkedArrayQueueProducerFields"),
                TypeReference.of("io.grpc.netty.shaded.io.netty.util.internal.shaded.org.jctools.queues.BaseMpscLinkedArrayQueueConsumerFields"),
                TypeReference.of("io.grpc.netty.shaded.io.netty.util.internal.shaded.org.jctools.queues.BaseMpscLinkedArrayQueueColdProducerFields"),

                TypeReference.of("io.grpc.netty.shaded.io.netty.buffer.AbstractByteBufAllocator"),

                TypeReference.of("org.bouncycastle.jsse.BCApplicationProtocolSelector"),
                TypeReference.of("org.bouncycastle.jsse.BCSSLEngine"),
                TypeReference.of("com.sun.jndi.dns.DnsContextFactory"),
                TypeReference.of("com.sun.jndi.url.dns.dnsURLContextFactory"),
                TypeReference.of("io.grpc.netty.shaded.io.netty.util.ReferenceCountUtil"),

                )
        ) {
            it.withMembers(*MemberCategory.entries.toTypedArray())
        }
    }
}