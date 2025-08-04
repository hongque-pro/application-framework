dependencies {
    api(project(":core-starter"))
    api(project(":identity-starter"))
    api("com.labijie.infra:oauth2-resource-server-starter:${Versions.infraOAuth2}")
    api("org.springdoc:springdoc-openapi-starter-webmvc-ui:${Versions.springDoc}")
    api("org.springframework.boot:spring-boot-starter-undertow")
    testImplementation(project(":auth-starter"))
}
