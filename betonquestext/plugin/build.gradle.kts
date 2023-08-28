plugins {
    id("cc.mewcraft.deploy-conventions")
    id("cc.mewcraft.paper-plugins")
}

project.ext.set("name", "BetonQuestExt")

version = "1.4.0"
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
