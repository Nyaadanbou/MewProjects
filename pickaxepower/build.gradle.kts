plugins {
    id("cc.mewcraft.repo-conventions")
    id("cc.mewcraft.java-conventions")
    id("cc.mewcraft.deploy-conventions")
    id("cc.mewcraft.paper-plugins")
}

project.ext.set("name", "PickaxePower")

group = "cc.mewcraft"
version = "1.0.1"
description = "Add pickaxe power system to your pickaxes!"

dependencies {
    compileOnly(project(":mewcore"))

    compileOnly(libs.server.paper)

    // 3rd party plugins
    compileOnly(libs.helper) { isTransitive = false }
    compileOnly(libs.protocollib)
    compileOnly(libs.itemsadder)
}
