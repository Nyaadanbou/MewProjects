plugins {
    id("cc.mewcraft.deploy-conventions")
    id("cc.mewcraft.paper-plugins")
}

project.ext.set("name", "MythicLibExt")

group = "cc.mewcraft.mythiclibext"
version = "1.0.0"
description = "An extension of MythicLib plugin"

dependencies {
    // server api
    compileOnly(libs.server.paper)

    // dependent module
    compileOnly(project(":rpgext:common"))

    // libs that present as other plugins
    compileOnly(project(":mewcore"))
    compileOnly(libs.helper)
    compileOnly(libs.mythiclib)
    compileOnly(libs.itemsadder)
}
