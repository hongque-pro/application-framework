processResources {
    from(rootProject.getRootDir().toString() + "/sql-scripts") {
        include("open.sql")
    }
}

dependencies {
    api(project(":auth-starter"))
}