plugins {
    id("org.springframework.boot") version Versions.springBoot
    id("org.graalvm.buildtools.native") version Versions.nativeBuildTools
}
graalvmNative {
    binaries {
        named("main") {
            sharedLibrary.set(false)
            mainClass = "com.labijie.application.dummy.DummyApplicationKt"

            //https://github.com/microcks/microcks/issues/1054
//            buildArgs.add("--initialize-at-run-time=io.netty.handler.ssl.BouncyCastleAlpnSslUtils,org.slf4j.LoggerFactory")
 //            buildArgs.add("--initialize-at-build-time=org.bouncycastle")
 //            buildArgs.add("--initialize-at-run-time=org.bouncycastle.jcajce.provider.drbg.DRBG\$Default,org.bouncycastle.jcajce.provider.drbg.DRBG\$NonceAndIV")
        }
    }

    metadataRepository {
        version.set("0.3.22")
    }

}

dependencies {
     implementation(project(":auth-starter"))
//    implementation project(":payment-starter")
//    implementation project(":payment-car-starter")
//    implementation project(":order-starter")
//    implementation project(":kaptcha-human-starter")
//    implementation project(":open-api-starter")
    implementation("com.h2database:h2")
    implementation(project(":dapr-starter"))
    //implementation(project(":minio-starter"))
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")

    implementation(project(":email-starter"))
}


val javaLauncher = javaToolchains.launcherFor {
    languageVersion = JavaLanguageVersion.of(17)
}

val jar = tasks.withType<AbstractArchiveTask>().firstOrNull()


//tasks.register("dapr") {
//    group = "dapr"
//    dependsOn(jar?.name)
//    description = "Run the output executable jar with dapr"
//    exec {
//        val javaHome = javaLauncher.get().metadata.installationPath.asFile.absolutePath
//        val separator = File.separator
//        val java = "${javaHome}${separator}bin${separator}java"
//        val jar = "${jar?.archiveFile?.get()?.asFile?.absolutePath}"
//        println("cmd: ${javaHome}${separator}bin${separator}java -jar $jar")
//        setCommandLine(java, "-Dfile.encoding=utf-8", "-jar", jar)
//    }
//}
