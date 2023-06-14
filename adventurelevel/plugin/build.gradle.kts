plugins {
    id("cc.mewcraft.deploy-conventions")
}

project.ext.set("name", "AdventureLevel")

dependencies {
    // api module
    implementation(project(":adventurelevel:api"))
    implementation(project(":adventurelevel:hooks"))

    // my own libs
    compileOnly(project(":mewcore"))

    // server api
    compileOnly(libs.server.paper)

    // libs to shade into the jar
    implementation(libs.evalex)

    // libs that present as other plugins
    compileOnly(libs.helper) { isTransitive = false }
    compileOnly(libs.helper.redis)
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
