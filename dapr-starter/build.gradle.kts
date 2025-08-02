//infra {
//    useSpringConfigurationProcessor(Versions.springBoot)
//}

dependencies {
    api("io.dapr:dapr-sdk-springboot:${Versions.dapr}") {
        exclude(group="org.springframework")
        exclude(group="org.springframework.boot")
        exclude(group="ch.qos.logback")
        exclude(group="org.slf4j")
    }

//    implementation("io.grpc:grpc-netty") {
//        exclude(group="ch.qos.logback")
//        exclude(group="org.slf4j")
//    }

    api("org.springframework.boot:spring-boot-starter-web")
    api(project(":core-starter"))

    compileOnly(project(":core-web-starter"))
    compileOnly("com.labijie.infra:oauth2-authorization-server-starter:${Versions.infraOAuth2}")
    compileOnly("org.graalvm.nativeimage:svm:${Versions.graalvmSvm}")

}


//tasks.compileKotlin {
//    inputs.files(tasks.named("processResources"))
//}