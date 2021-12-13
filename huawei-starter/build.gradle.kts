dependencies {
    api("com.labijie.infra:commons-core-starter:${Versions.infraCommons}")
    api("com.huaweicloud:esdk-obs-java:${HuaweiSDKVersions.esdk}")
    compileOnly("org.springframework.boot:spring-boot-starter-web")
    api(project(":core"))
}