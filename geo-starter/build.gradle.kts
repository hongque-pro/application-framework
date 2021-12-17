processResources {
    from(rootProject.rootDir.toString() + "/sql-scripts") {
        include("geo.sql")
    }
}

dependencies {
    api(project(":core"))
    api("com.uber:h3:${Versions.uberH3}")
}