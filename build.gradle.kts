
plugins {
    id("com.labijie.infra") version Versions.infraPlugin
    id("com.gorylenko.gradle-git-properties") version Versions.gitPropertiesPlugin apply false
}

allprojects {
    group = "com.labijie.application"
    version = "2.2.1"
}

allprojects {
    infra {
        useDefault {
            includeSource = true
            includeDocument = true
            infraBomVersion = Versions.infraBom
            kotlinVersion = Versions.kotlin
        }
        //usePublishingPlugin()
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
            }

            //toGitHubPackages("hongque-pro", "application-framework")
        }
//        if(mybatisConfigFile.exists()){
//            val propertiesFile = File(project.rootProject.projectDir, ("mybatis-conf/settings.properties").replace('/', File.separatorChar))
//            useMybatis(mybatisConfigFile.absolutePath, propertiesFile.absolutePath)
//        }
    }


    dependencies {
        add("testImplementation", "org.springframework.boot:spring-boot-starter-test")
    }
}



