dependencies {
    // the server api
    compileOnly(libs.server.paper)

    // core libs
    compileOnly(project(":mewcore"))

    compileOnly(libs.helper)
}