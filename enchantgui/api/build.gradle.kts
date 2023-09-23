dependencies {
    // the server api
    compileOnly(libs.server.paper)

    // libs present as other plugins
    compileOnly(libs.helper)
    compileOnly(project(":mewcore"))
}