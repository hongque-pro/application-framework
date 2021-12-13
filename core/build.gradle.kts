plugins {
    id("com.gorylenko.gradle-git-properties")
}
dependencies {
    api("com.labijie.infra:commons-core-starter:${Versions.infraCommons}")
    api("com.labijie.infra:commons-snowflake-starter:${Versions.infraCommons}")

    api("com.labijie.infra:commons-mybatis-dynamic-starter:${Versions.infraCommons}")

    api("org.springframework.cloud:spring-cloud-stream")
    api("org.springframework.cloud:spring-cloud-commons")

    api("com.labijie:caching-kotlin-core-starter:${Versions.infraCaching}")
    api("org.hibernate.validator:hibernate-validator")
    api("org.owasp.antisamy:antisamy:${Versions.antisamy}") {
        exclude(module = "slf4j-simple")
    }

    api("com.squareup.okhttp3:okhttp")
    api("org.springframework:spring-web")
    api("org.bouncycastle:bcpkix-jdk15on:${Versions.bouncyCastle}")


    api("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")

    api("mysql:mysql-connector-java") {
        exclude(module= "protobuf-java")
    }
}