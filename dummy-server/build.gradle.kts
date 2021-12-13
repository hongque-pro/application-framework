dependencies {
     implementation(project(":auth-starter"))
//    implementation project(":payment-starter")
//    implementation project(":payment-car-starter")
//    implementation project(":order-starter")
//    implementation project(":kaptcha-human-starter")
//    implementation project(":open-api-starter")
    implementation("com.h2database:h2")
    implementation(project(":core-web"))
    //implementation(project(":minio-starter"))
    implementation("com.labijie.infra:spring-cloud-stream-binder-core:${Versions.infraCloudStream}")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")
}