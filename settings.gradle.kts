rootProject.name = "application-framework"
include("core")
include("core-web")
include("identity-starter")
include("core-starter")
include("core-aliyun")
include("aliyun-starter")
include("auth-starter")
include("auth-social-starter")

include("payment-testing")
include("payment-starter")
include("order-starter")
include("payment-car-starter")

include("core-excel-starter")
include("geo-starter")
include("kaptcha-human-starter")
include("open-api-starter")
include("huawei-starter")
include("minio-starter")


include("dummy-server")

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}