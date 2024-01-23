//infra {
//    useSpringConfigurationProcessor(Versions.springBoot)
//}

dependencies {
    api("io.dapr:dapr-sdk-springboot:${Versions.dapr}") {
        exclude(group="org.springframework")
        exclude(group="org.springframework.boot")
    }
    api("org.springframework.boot:spring-boot-starter-web")
    api(project(":core-starter"))

    compileOnly(project(":core-web-starter"))
    compileOnly("com.labijie.infra:oauth2-authorization-server-starter:${Versions.infraOAuth2}")
}

//tasks.compileKotlin {
//    inputs.files(tasks.named("processResources"))
//}