plugins {
    id("cc.mewcraft.java-conventions")
    id("cc.mewcraft.deploy-conventions")
    id("cc.mewcraft.repository-conventions")
    id("cc.mewcraft.paper-plugins")
}

project.ext.set("name", "KeepInv")

group = "cc.mewcraft"
version = "1.0.0"
description = "Provides special handling of the keep-inventory feature"

dependencies {
    compileOnly(project(":mewcore"))
    compileOnly(libs.server.paper)

    // 3rd party plugins
    compileOnly(libs.helper) { isTransitive = false }
    compileOnly(libs.itemsadder)
}
