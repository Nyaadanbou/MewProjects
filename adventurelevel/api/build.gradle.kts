plugins {
    id("cc.mewcraft.publishing-conventions")
}

version = "1.1.0"

dependencies {
    compileOnly(project(":mewcore"))

    // server api
    compileOnly(libs.server.paper)

    // libs that present as other plugins
    compileOnly(libs.helper) { isTransitive = false }
}
