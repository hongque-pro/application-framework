
processResources {
    from(rootProject.rootDir.toString() + "/sql-scripts") {
        include("auth.sql")
    }
}

dependencies {
    api(project(":core"))

    testImplementation("com.h2database:h2")
}