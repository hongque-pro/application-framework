plugins {
    id("org.springframework.boot") version Versions.springBoot
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
