plugins {
    id("cc.mewcraft.deploy-conventions")
    id("cc.mewcraft.paper-plugins")
}

project.ext.set("name", "MythicMobsExt")

group = "cc.mewcraft.mythicmobsext"
version = "1.1.0"
description = "An extension of MythicMobs plugin"

dependencies {
    // server api
    compileOnly(libs.server.paper)

    // dependent module
    compileOnly(project(":rpgext:common"))

    // libs that present as other plugins
    compileOnly(project(":mewcore"))
    compileOnly(libs.helper)
    compileOnly(libs.mmoitems)
    compileOnly(libs.mythiclib)
    compileOnly(libs.mythicmobs)
}
