plugins {
    id("cc.mewcraft.deploy-conventions")
    id("cc.mewcraft.paper-plugins")
}

project.ext.set("name", "TownyLink")

group = "cc.mewcraft.townylink"
version = "1.2.0"
description = "Sync Towny data between your server network"

dependencies {
    implementation(project(":townyext:towny-link:api"))

    // server api
    compileOnly(libs.server.paper)

    // my own libs
    compileOnly(project(":mewcore"))

    // plugin libs
    compileOnly(libs.towny)
    compileOnly(libs.helper) { isTransitive = false }
    compileOnly(libs.helper.redis)
    compileOnly(libs.luckperms)
    compileOnly(libs.connector.bukkit)
    compileOnly(libs.huskhomes)
}
