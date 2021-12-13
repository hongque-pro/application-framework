dependencies {
    api(project(":core"))
    api("org.springframework.boot:spring-boot-starter-web")
    api("com.labijie.infra:oauth2-resource-server-starter:${Versions.infraOAuth2}")

//    implementation("org.springdoc:springdoc-openapi-ui:${Versions.springDoc}")
//    implementation("org.springdoc:springdoc-openapi-kotlin:${Versions.springDoc}")
//    implementation("org.springdoc:springdoc-openapi-security:${Versions.springDoc}")
    implementation("io.springfox:springfox-boot-starter:${Versions.springfox}")
    implementation("io.springfox:springfox-swagger2:${Versions.springfox}")
    implementation("io.springfox:springfox-swagger-ui:${Versions.springfox}")
}