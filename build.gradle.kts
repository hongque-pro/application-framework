
plugins {
    id("com.labijie.infra") version Versions.infraPlugin
}

allprojects {
    group = "com.labijie.application"
    version = "3.2.5"
}

allprojects {
    infra {
        useDefault {
            includeSource = true
            includeDocument = true
            infraBomVersion = Versions.infraBom
            useMavenProxy = false
        }
    }


//    forceDependencyVersion(group = "io.netty", "4.1.80.Final")
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



