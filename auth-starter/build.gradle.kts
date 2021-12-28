dependencies {
    api(project(":identity-starter"))
//    compile project(":commons-data")
    api("com.labijie.infra:oauth2-authorization-server-starter:${Versions.infraOAuth2}")
    api(project(":core-web"))
}