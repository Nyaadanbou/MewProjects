plugins {
    id("cc.mewcraft.java-conventions")
    id("cc.mewcraft.deploy-conventions")
    id("cc.mewcraft.repository-conventions")
    id("cc.mewcraft.paper-plugins")
}

project.ext.set("name", "MewUtils")

group = "cc.mewcraft"
version = "1.23.1"
description = "A plugin consisting of many small features"

dependencies {
    implementation(project(":mewutils:base"))

    // libs from core
    compileOnly(libs.configurate)
    compileOnly(libs.anvilgui)

    // 3rd party plugins
    compileOnly(libs.luckperms)
    compileOnly(libs.vault) { isTransitive = false }
    compileOnly(libs.papi) { isTransitive = false }
    compileOnly(libs.itemsadder)
    compileOnly(libs.protocollib)
    compileOnly(libs.essentials) { isTransitive = false }
}
