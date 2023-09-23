plugins {
    id("cc.mewcraft.repo-conventions")
    id("cc.mewcraft.java-conventions")
    id("cc.mewcraft.deploy-conventions")
}

project.ext.set("name", "persistentserver")

group = "cc.mewcraft"
version = "1.0.0"
description = "Makes players log back in to the server they logged out from"

dependencies {
    compileOnly(libs.proxy.velocity)
    annotationProcessor(libs.proxy.velocity)
}
