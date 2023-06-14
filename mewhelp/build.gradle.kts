plugins {
    id("cc.mewcraft.java-conventions")
    id("cc.mewcraft.deploy-conventions")
    id("cc.mewcraft.repository-conventions")
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
