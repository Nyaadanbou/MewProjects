plugins {
    id("cc.mewcraft.repo-conventions")
    id("cc.mewcraft.java-conventions")
    id("cc.mewcraft.deploy-conventions")
    id("cc.mewcraft.paper-plugins")
}

project.ext.set("name", "MMOExt")

group = "cc.mewcraft"
version = "2.8.0"
description = "An extension of MMO plugins."

dependencies {
    // API
    compileOnly(libs.server.paper)

    // Plugin library
    compileOnly(project(":mewcore"))
    compileOnly(libs.helper)

    // External plugins
    compileOnly(libs.itemsadder)
    compileOnly(libs.mythicmobs)
    compileOnly(libs.mythiclib)
    compileOnly(libs.mmoitems)
    compileOnly(libs.towny)
}
