plugins {
    id("io.freefair.lombok")
    id("cc.mewcraft.deploy-conventions")
}

project.ext.set("name", "BetonQuestExt")

version = "1.3.1"
description = "Brings BetonQuest more integrations with 3rd party plugins"

dependencies {
    implementation(project(":betonquestext:common"))

    // sub modules
    implementation(project(":betonquestext:adventurelevel"))
    implementation(project(":betonquestext:brewery"))
    implementation(project(":betonquestext:itemsadder"))

    // libs to shade into the jar
    implementation(libs.evalex)
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
