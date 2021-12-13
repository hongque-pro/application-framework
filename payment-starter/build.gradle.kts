dependencies {
    api(project(":core"))
    compileOnly(project(":core-web"))
    api("io.netty:netty-codec-http")
    testImplementation(project(":payment-testing"))
}