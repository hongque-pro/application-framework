infra {
    useSpringConfigurationProcessor(Versions.springBoot)
}

dependencies {
    api(project(":core-starter"))
    api(project(":identity-starter"))
    compileOnly(project(":core-web-starter"))
}