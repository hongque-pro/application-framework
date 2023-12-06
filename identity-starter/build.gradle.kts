
infra {
    useInfraOrmGenerator(Versions.infraOrm)
}

dependencies {
    api(project(":core-starter"))
    api("org.springframework.security:spring-security-crypto")
    testImplementation("com.h2database:h2")
    testImplementation("com.labijie.orm:exposed-springboot-test-starter:${Versions.infraOrm}")
}