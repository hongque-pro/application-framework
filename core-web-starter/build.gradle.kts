dependencies {
    api(project(":core-starter"))
    api(project(":identity-starter"))
    api("org.springframework.boot:spring-boot-starter-web")
    api("com.labijie.infra:oauth2-resource-server-starter:${Versions.infraOAuth2}")
    api("org.springdoc:springdoc-openapi-starter-webmvc-ui:${Versions.springDoc}") {
        exclude("org.springframework.boot")
    }
//    implementation("com.pig4cloud.plugin:captcha-core:${Versions.easyCaptcha}") {
//        exclude("jakarta.servlet")
//        exclude("org.slf4j")
//        exclude("javax.servlet")
//    }

    testImplementation(project(":auth-starter"))
}