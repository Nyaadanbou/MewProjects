plugins {
    id("cc.mewcraft.repo-conventions")
    id("cc.mewcraft.java-conventions")
    id("cc.mewcraft.deploy-conventions")
    id("cc.mewcraft.paper-plugins")
}

project.ext.set("name", "MewHelp")

group = "cc.mewcraft"
version = "1.1.0"
description = "Allows to create custom help messages (with MiniMessage support)"

dependencies {
    compileOnly(project(":mewcore"))
    compileOnly(libs.server.paper)
    compileOnly(libs.helper)
}
