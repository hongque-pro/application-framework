package com.labijie.application.dapr.aot

import org.graalvm.nativeimage.hosted.Feature
import org.graalvm.nativeimage.hosted.RuntimeClassInitialization

/**
 * @author Anders Xiao
 * @date 2025/6/21
 */
@Suppress("unused")
class InfraDaprFeature : Feature {
    override fun beforeAnalysis(access: Feature.BeforeAnalysisAccess?) {

        val classes = """
            io.grpc.netty.shaded.io.netty.bootstrap,io.grpc.netty.shaded.io.netty.buffer,io.grpc.netty.shaded.io.netty.channel,io.grpc.netty.shaded.io.netty.channel.socket.nio,io.grpc.netty.shaded.io.netty.handler,io.grpc.netty.shaded.io.netty.handler.ssl,io.grpc.netty.shaded.io.netty.internal.tcnative,io.grpc.netty.shaded.io.netty.resolver,io.grpc.netty.shaded.io.netty.util.internal,io.grpc.netty.shaded.io.netty.util.internal.logging,io.grpc.netty.shaded.io.netty.util,io.grpc.netty.shaded.io.netty.util.concurrent --trace-class-initialization=ch.qos.logback.classic.PatternLayout,ch.qos.logback.core.subst.Token,ch.qos.logback.core.pattern.parser.Parser,ch.qos.logback.core.model.processor.DefaultProcessor${'$'}1,org.springframework.boot.logging.logback.ColorConverter,ch.qos.logback.classic.Level,ch.qos.logback.classic.Logger,ch.qos.logback.core.CoreConstants,ch.qos.logback.core.status.StatusBase,ch.qos.logback.core.model.processor.ChainedModelFilter${'$'}1,ch.qos.logback.core.util.StatusPrinter,ch.qos.logback.core.util.Loader,ch.qos.logback.core.status.InfoStatus,ch.qos.logback.classic.model.processor.LogbackClassicDefaultNestedComponentRules,ch.qos.logback.core.util.Duration,ch.qos.logback.core.subst.NodeToStringTransformer${'$'}1,org.slf4j.LoggerFactory,ch.qos.logback.core.model.processor.ImplicitModelHandler${'$'}1,ch.qos.logback.core.subst.Parser${'$'}1
        """.trimIndent()

        val packages = classes.split(",").toTypedArray()

        RuntimeClassInitialization.initializeAtRunTime(
            *packages
        )
    }
}