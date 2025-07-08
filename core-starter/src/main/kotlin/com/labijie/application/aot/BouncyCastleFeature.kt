package com.labijie.application.aot

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.graalvm.nativeimage.ImageSingletons
import org.graalvm.nativeimage.hosted.Feature
import org.graalvm.nativeimage.hosted.Feature.AfterRegistrationAccess
import org.graalvm.nativeimage.hosted.RuntimeClassInitialization
import org.graalvm.nativeimage.impl.RuntimeClassInitializationSupport
import java.security.Security


/**
 * @author Anders Xiao
 * @date 2025/6/21
 */
@Suppress("unused")
public class BouncyCastleFeature : Feature {

    private val classNames = listOf(
        "io.grpc.netty.shaded.io.netty.handler.ssl.BouncyCastleAlpnSslUtils",
        "io.netty.handler.ssl.BouncyCastleAlpnSslUtils"
    )

    override fun beforeAnalysis(access: Feature.BeforeAnalysisAccess?) {
        RuntimeClassInitialization.initializeAtBuildTime("org.bouncycastle")
        RuntimeClassInitialization.initializeAtRunTime("org.bouncycastle.jcajce.provider.drbg.DRBG\$Default", "org.bouncycastle.jcajce.provider.drbg.DRBG\$NonceAndIV")
    }
    override fun afterRegistration(access: AfterRegistrationAccess?) {
        Security.addProvider(BouncyCastleProvider())
    }
}