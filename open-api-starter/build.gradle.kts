infra {
    useInfraOrmGenerator(Versions.infraOrm)
}

dependencies {
    api(project(":core-web-starter"))
    testImplementation("com.h2database:h2")
    testImplementation("com.labijie.orm:exposed-springboot-test-starter:${Versions.infraOrm}")
}