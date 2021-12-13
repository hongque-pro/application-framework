dependencies {
    api(project(":core"))
    api("com.aliyun:aliyun-java-sdk-core:${AliyunSDKVersions.core}")
    api("com.aliyun:aliyun-java-sdk-sts:${AliyunSDKVersions.sts}")
    api("com.aliyun.oss:aliyun-sdk-oss:${AliyunSDKVersions.oss}")

    api("com.labijie.infra:commons-core-starter:${Versions.infraCommons}")
    compileOnly("org.springframework.boot:spring-boot-starter-web")
}