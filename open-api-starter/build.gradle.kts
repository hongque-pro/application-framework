infra {
    useInfraOrmGenerator(Versions.infraOrm)
}

dependencies {
    api(project(":core-web-starter"))
    api("com.labijie.orm:exposed-springboot-starter:${Versions.infraOrm}")
    testImplementation("com.h2database:h2")
    testImplementation("com.labijie.orm:exposed-springboot-test-starter:${Versions.infraOrm}")
}