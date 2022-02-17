processResources {
    from(rootProject.rootDir.toString() + "/sql-scripts") {
        include("open.sql")
    }
}

dependencies {
    api(project(":auth-starter"))

    testImplementation("com.h2database:h2")
    //testImplementation("mybatis-spring-boot-starter-test:org.mybatis.spring.boot")
}