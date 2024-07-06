
plugins {
    id("com.labijie.infra") version Versions.infraPlugin
}

allprojects {
    group = "com.labijie.application"
    version = "3.1.4"
}

allprojects {
    infra {
        useDefault {
            includeSource = true
            includeDocument = true
            infraBomVersion = Versions.infraBom
            kotlinVersion = Versions.kotlin
            useMavenProxy = false
        }
    }
}



subprojects {

    infra {
        if (!project.name.startsWith("dummy")) {
            publishing {
                pom {
                    description = "application framework package"
                    githubUrl("hongque-pro", "application-framework")
                    artifactId { "framework-${it.name}" }
                }
                toGithubPackages("hongque-pro", "application-framework")
            }
        }
    }


    dependencies {
        add("testImplementation", "org.springframework.boot:spring-boot-starter-test")
    }
}



