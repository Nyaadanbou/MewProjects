plugins {
    id("cc.mewcraft.deploy-conventions")
    id("cc.mewcraft.paper-plugins")
}

project.ext.set("name", "EnchantGui")

dependencies {
    // dependent module
    implementation(project(":enchantgui:api"))
    runtimeOnly(project(":enchantgui:provider"))

    // the server api
    compileOnly(libs.server.paper)

    // core libs
    compileOnly(project(":mewcore"))

    // libs to be shaded
    implementation(libs.bundles.invui)

    // libs present as other plugins
    compileOnly(libs.helper)
}