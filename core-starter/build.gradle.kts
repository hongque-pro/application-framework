infra {
    useInfraOrmGenerator(Versions.infraOrm)
    processSpringMessageSource("com/labijie/application")
//    useSpringConfigurationProcessor("3.5.0")
}

dependencies {
    compileOnly("org.graalvm.nativeimage:svm:24.2.1")

    api("io.github.classgraph:classgraph:${Versions.classGraph}")
    api("com.labijie.infra:commons-springboot-starter:${Versions.infraCommons}")
    api("commons-validator:commons-validator:${ApacheVersions.commonsValidator}") {
        exclude(module = "commons-logging")
    }
    api("com.googlecode.libphonenumber:libphonenumber:${Versions.libphonenumber}")
    api("org.jsoup:jsoup:${Versions.jsoup}")
    api("org.springframework:spring-tx")
    api("com.labijie:caching-kotlin-core-starter:${Versions.infraCaching}")
    api("org.hibernate.validator:hibernate-validator")

    //for web XXS injection
    api("org.owasp.antisamy:antisamy:${Versions.antisamy}") {
        exclude(module = "slf4j-simple")
    }

    api("com.squareup.okhttp3:okhttp")
    //api("io.projectreactor.netty:reactor-netty-http")
    api("org.springframework:spring-web")
    api("org.bouncycastle:bcprov-jdk18on")


    api("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")

    api("com.mysql:mysql-connector-j:${Versions.mysqlVersion}") {
        exclude(module= "protobuf-java")
    }

    api("com.labijie.orm:exposed-springboot-starter:${Versions.infraOrm}")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-logging")
}