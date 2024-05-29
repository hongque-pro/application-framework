import org.gradle.internal.impldep.com.fasterxml.jackson.databind.ObjectMapper

infra {
    useInfraOrmGenerator(Versions.infraOrm)
    processSpringMessageSource("com/labijie/application")
}

dependencies {
    api("com.labijie.infra:commons-springboot-starter:${Versions.infraCommons}")
    api("commons-validator:commons-validator:${ApacheVersions.commonsValidator}")
    api("com.googlecode.libphonenumber:libphonenumber:${Versions.libphonenumber}")
    api("org.jsoup:jsoup:${Versions.jsoup}")
    api("org.springframework:spring-tx")
    api("com.labijie:caching-kotlin-core-starter:${Versions.infraCaching}")
    api("org.hibernate.validator:hibernate-validator")
    //for web XXS injection
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

    api("com.labijie.orm:exposed-springboot-starter:${Versions.infraOrm}")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-logging")
}