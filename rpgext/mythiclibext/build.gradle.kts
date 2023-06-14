plugins {
    id("cc.mewcraft.deploy-conventions")
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

tasks {
    processResources {
        filesMatching("**/paper-plugin.yml") {
            expand(
                mapOf(
                    "version" to "${project.version}",
                    "description" to project.description
                )
            )
        }
    }
}
