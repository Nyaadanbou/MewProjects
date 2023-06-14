plugins {
    id("cc.mewcraft.java-conventions")
    id("cc.mewcraft.deploy-conventions")
    id("cc.mewcraft.repository-conventions")
}

project.ext.set("name", "MewUtils")

group = "cc.mewcraft"
version = "1.23.1"
description = "A plugin consisting of many small features"

dependencies {
    implementation(project(":mewutils:base"))

    // 3rd party plugins
    compileOnly(libs.luckperms)
    compileOnly(libs.vault) { isTransitive = false }
    compileOnly(libs.papi) { isTransitive = false }
    compileOnly(libs.itemsadder)
    compileOnly(libs.protocollib)
    compileOnly(libs.essentials) { isTransitive = false }
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
