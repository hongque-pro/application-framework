//infra {
//    useKaptPlugin("org.springframework.boot:spring-boot-configuration-processor:${Versions.springBoot}")
//}

dependencies {
    api("io.dapr:dapr-sdk-springboot:${Versions.dapr}") {
        exclude(group="org.springframework")
        exclude(group="org.springframework.boot")
    }
    api(project(":core-web-starter"))
}

//tasks.compileKotlin {
//    inputs.files(tasks.named("processResources"))
//}