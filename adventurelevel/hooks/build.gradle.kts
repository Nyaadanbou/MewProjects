plugins {
    id("cc.mewcraft.publishing-conventions")
}

dependencies {
    // api module
    implementation(project(":adventurelevel:api"))

    // my own libs
    compileOnly(project(":mewcore"))

    // server api
    compileOnly(libs.server.paper)

    // libs that present as other plugins

    // the basic utils
    compileOnly(libs.helper) { isTransitive = false }

    // to register context
    compileOnly(libs.luckperms)

    // to add placeholders
    compileOnly(libs.papi) { isTransitive = false }
    compileOnly(libs.minipapi) { isTransitive = false }

    // to implement RPGHandler and RPGPlayer
    compileOnly(libs.mmoitems)
}
