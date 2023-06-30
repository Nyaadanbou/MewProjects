plugins {
    id("cc.mewcraft.deploy-conventions")
    id("cc.mewcraft.paper-plugins")
}

project.ext.set("name", "TownyBoard")

group = "cc.mewcraft.townyboard"
version = "1.0.0"
description = "Add fancier boards to towns and nations!"

dependencies {
    // server api
    compileOnly(libs.server.paper)

    // my own libs
    compileOnly(project(":mewcore"))

    // libs that present as other plugins
    compileOnly(libs.towny)
    compileOnly(libs.helper)
    compileOnly(libs.luckperms)
    compileOnly(libs.vault) { isTransitive = false }
    compileOnly(libs.papi)
}
