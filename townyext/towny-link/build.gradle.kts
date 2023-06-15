plugins {
    id("cc.mewcraft.paper-plugins")
}

project.ext.set("name", "TownyLink")

group = "cc.mewcraft.townylink"
version = "1.1.0"
description = "Sync Towny data between your server network"

dependencies {
    // server api
    compileOnly(libs.server.paper)

    // my own libs
    compileOnly(libs.mewcore)

    // plugin libs
    compileOnly(libs.towny)
    compileOnly(libs.helper) { isTransitive = false }
    compileOnly(libs.connector.bukkit)
}
