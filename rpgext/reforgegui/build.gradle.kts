plugins {
    id("cc.mewcraft.deploy-conventions")
    id("cc.mewcraft.paper-plugins")
}

project.ext.set("name", "ReforgeGui")

group = "cc.mewcraft.reforge.gui"
version = "1.0.1"
description = "Provides GUIs for the item reforge mechanism"

dependencies {
    // dependent module
    compileOnly(project(":rpgext:common"))

    // core libs
    compileOnly(project(":mewcore"))
    // libs embedded in core
    compileOnly(libs.bundles.invui)
    compileOnly(libs.commons.io)

    // server api
    compileOnly(libs.server.paper)

    // libs that present as other plugins
    compileOnly(project(":rpgext:reforge"))
    compileOnly(project(":economy:api"))
    compileOnly(libs.helper)
    // compileOnly(libs.mmoitems)
    // compileOnly(libs.mythiclib)
    // compileOnly(libs.mythicmobs)
}
