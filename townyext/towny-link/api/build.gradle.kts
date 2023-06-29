dependencies {
    // server api
    compileOnly(libs.server.paper)

    // core libs
    compileOnly(project(":mewcore"))

    // plugin libs
    compileOnly(libs.helper) { isTransitive = false }
    compileOnly(libs.helper.redis)
    compileOnly(libs.luckperms)
}
