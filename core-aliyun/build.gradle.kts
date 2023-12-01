dependencies {
    api(project(":core"))
    api("com.aliyun:aliyun-java-sdk-core:${AliyunSDKVersions.core}")
    api("com.aliyun:aliyun-java-sdk-sts:${AliyunSDKVersions.sts}")
    api("com.aliyun.oss:aliyun-sdk-oss:${AliyunSDKVersions.oss}")

    compileOnly("org.springframework.boot:spring-boot-starter-web")
}