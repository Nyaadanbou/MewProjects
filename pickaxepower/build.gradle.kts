plugins {
    id("cc.mewcraft.java-conventions")
    id("cc.mewcraft.deploy-conventions")
    id("cc.mewcraft.repository-conventions")
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

tasks {
    processResources {
        filesMatching("**/paper-plugin.yml") {
            val mappings = mapOf(
                "version" to "${project.version}",
                "description" to project.description
            )
            expand(mappings)
        }
    }
}
