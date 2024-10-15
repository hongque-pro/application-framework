dependencies {
    api(project(":identity-starter"))
    api("org.springframework.boot:spring-boot-starter-web")
    api("com.labijie.infra:oauth2-resource-server-starter:${Versions.infraOAuth2}")

    api("org.springdoc:springdoc-openapi-starter-webmvc-ui:${Versions.springDoc}") {
        exclude("org.springframework.boot")
    }
    implementation("io.springboot.plugin:captcha-core:${Versions.easyCaptcha}") {
        exclude("jakarta.servlet")
        exclude("org.slf4j")
    }
}