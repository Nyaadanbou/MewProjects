plugins {
    id("cc.mewcraft.java-conventions")
    id("cc.mewcraft.repository-conventions")
    alias(libs.plugins.paperweight.userdev)
}

project.ext.set("name", "MewFishing")

group = "cc.mewcraft"
description = "Let's customize fishing!"
version = "1.15.0"

dependencies {
    // NMS
    paperweight.paperDevBundle("1.19.4-R0.1-SNAPSHOT")

    // Server API
    compileOnly(libs.server.paper)

    // Plugin libs
    compileOnly(project(":mewcore"))
    compileOnly(libs.helper)
    compileOnly(libs.helper.redis)

    // 3rd party plugins
    compileOnly(libs.luckperms)
    compileOnly(libs.vault) { isTransitive = false }
    compileOnly(libs.towny)
    compileOnly(libs.gemseconomy)

    // Test
    testImplementation(libs.server.paper)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.helper)
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
    reobfJar {
        outputJar.set(layout.buildDirectory.file("${rootProject.name}-${project.version}.jar"))
    }
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
    register("deployJar") {
        doLast {
            exec {
                commandLine("rsync", reobfJar.get().outputJar.get().asFile.absoluteFile, "dev:data/dev/jar")
            }
        }
    }
    register("deployJarFresh") {
        dependsOn(build)
        finalizedBy(named("deployJar"))
    }
}
