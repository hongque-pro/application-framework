dependencies {
    api(project(":identity-starter"))
    api("org.springframework.boot:spring-boot-starter-web")
    api("com.labijie.infra:oauth2-resource-server-starter:${Versions.infraOAuth2}")

    api("org.springdoc:springdoc-openapi-starter-webmvc-ui:${Versions.springDoc}")
}