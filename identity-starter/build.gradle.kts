
infra {
    useInfraOrmGenerator(Versions.infraOrm)
}

dependencies {
    api(project(":core"))
    api("com.labijie.orm:exposed-springboot-starter:${Versions.infraOrm}")
    api("org.springframework.security:spring-security-crypto")
    testImplementation("com.h2database:h2")
    testImplementation("com.labijie.orm:exposed-springboot-test-starter:${Versions.infraOrm}")
}