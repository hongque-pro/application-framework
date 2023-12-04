plugins {
    id("com.gorylenko.gradle-git-properties")
}
dependencies {
    api("com.labijie.infra:commons-springboot-starter:${Versions.infraCommons}")
    api("org.apache.commons:commons-text:${Versions.apacheCommonsText}")

//    api("org.springframework.cloud:spring-cloud-stream")
//    api("org.springframework.cloud:spring-cloud-commons")
//    api("com.labijie.infra:spring-cloud-stream-binder-core:${Versions.infraCloudStream}")

    api("org.jsoup:jsoup:${Versions.jsoup}")
    api("org.springframework:spring-tx")
    api("com.labijie:caching-kotlin-core-starter:${Versions.infraCaching}")
    api("org.hibernate.validator:hibernate-validator")
    api("org.owasp.antisamy:antisamy:${Versions.antisamy}") {
        exclude(module = "slf4j-simple")
    }

    api("io.projectreactor.netty:reactor-netty-http")
    api("org.springframework:spring-web")
    api("org.bouncycastle:bcprov-jdk18on:${Versions.bouncyCastle}")


    api("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")

    api("com.mysql:mysql-connector-j:${Versions.mysqlVersion}") {
        exclude(module= "protobuf-java")
    }


    compileOnly("com.labijie.orm:exposed-core:${Versions.infraOrm}")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-logging")
}