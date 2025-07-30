rootProject.name = "application-framework"
include("core-starter")
include("core-web-starter")
include("identity-starter")
//include("aliyun-starter")
include("auth-starter")
include("auth-social-starter")

//include("payment-testing")
//include("payment-starter")
//include("order-starter")
//include("payment-car-starter")

//include("geo-starter")
//include("kaptcha-human-starter")
//include("open-api-starter")

//include("minio-starter")


//include("core-aliyun")
//include("huawei-starter")

include("dummy-server")

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}
include("dapr-starter")

include("hcaptcha-starter")
include("email-starter")
include("sms-starter")