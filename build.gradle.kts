
plugins {
    id("com.labijie.infra") version Versions.infraPlugin
    id("com.gorylenko.gradle-git-properties") version Versions.gitPropertiesPlugin apply false
}

allprojects {
    group = "com.labijie.application"
    version = "2.2.0"

    infra {
        useDefault {
            includeSource = true
            infraBomVersion = Versions.infraBom
            kotlinVersion = Versions.kotlin
            useMavenProxy = false
        }

        useNexusPublish()
    }
}
subprojects {
    val mybatisConfigFile = File(this.projectDir, "mybatis-conf/config.xml")

    infra {
        if (!project.name.startsWith("dummy")) {
            usePublish {
                description = "infrastructure for oauth2 library"
                githubUrl("hongque-pro", "infra-oauth2")
            }
        }
        if(mybatisConfigFile.exists()){
            val propertiesFile = File(project.rootProject.projectDir, ("mybatis-conf/settings.properties").replace('/', File.separatorChar))
            useMybatis(mybatisConfigFile.absolutePath, propertiesFile.absolutePath)
        }
    }

    dependencies {
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.mybatis.spring.boot:mybatis-spring-boot-starter-test:${Versions.mybatisSpringBootTest}")
    }
}



