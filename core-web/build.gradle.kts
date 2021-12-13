dependencies {
    api(project(":core"))
    api("org.springframework.boot:spring-boot-starter-web")
    api("com.labijie.infra:oauth2-resource-server-starter:${Versions.infraOAuth2}")

    api("io.springfox:springfox-boot-starter:${Versions.springfox}")
}