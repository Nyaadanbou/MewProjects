plugins {
    id("cc.mewcraft.paper-plugins")
}

project.ext.set("name", "TownyBonus")

group = "cc.mewcraft.townybonus"
version = "1.3.0"
description = "Add bonus to towns and nations!"

dependencies {
    // server api
    compileOnly(libs.server.paper)

    // my own libs
    compileOnly(project(":mewcore"))
    // libs embedded in core
    compileOnly(libs.commons.io)

    // libs that present as other plugins
    compileOnly(libs.towny)
    compileOnly(libs.helper)
    compileOnly(libs.luckperms)
    compileOnly(libs.vault) { isTransitive = false }
    compileOnly(libs.papi)
}
