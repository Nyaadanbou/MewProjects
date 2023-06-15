plugins {
    id("cc.mewcraft.paper-plugins")
}

project.ext.set("name", "TownyOrigin")

group = "cc.mewcraft.townyorigin"
version = "1.0.0"
description = "Records the origin of players, powered by LuckPerms"

dependencies {
    // server api
    compileOnly(libs.server.paper)

    // my own libs
    compileOnly(project(":mewcore"))

    // plugin libs
    compileOnly(libs.towny)
    compileOnly(libs.helper) { isTransitive = false }
    compileOnly(libs.luckperms) // we use LuckPerms Metadata System to store the server-origin of players
    compileOnly(libs.papi)
    compileOnly(libs.minipapi)
}
