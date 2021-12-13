plugins {
    `kotlin-dsl`
}

fun getProxyMavenRepository(): String {
    val proxy: String? = System.getenv("MAVEN_PROXY")?.ifBlank { null }
    return proxy ?: "https://maven.aliyun.com/nexus/content/groups/public/"
}

repositories {
    mavenLocal()
    gradlePluginPortal()
    maven {
        this.setUrl(getProxyMavenRepository())
        this.isAllowInsecureProtocol = true
    }
    mavenCentral()
}
