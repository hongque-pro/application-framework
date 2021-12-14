dependencies {
    api(project(":core-web"))
    api(project(":identity-starter"))
//    compile project(":commons-data")
    api("com.labijie.infra:oauth2-authorization-server-starter:${Versions.infraOAuth2}")
    api("com.labijie.infra:oauth2-resource-server-starter:${Versions.infraOAuth2}")
}