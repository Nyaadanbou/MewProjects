plugins {
    id("cc.mewcraft.java-conventions")
    id("cc.mewcraft.repository-conventions")
    id("cc.mewcraft.paper-plugins")
    alias(libs.plugins.paperweight.userdev)
}

project.ext.set("name", "MewFish")

group = "cc.mewcraft"
description = "Let's customize fishing!"
version = "1.15.1"

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
        outputJar.set(layout.buildDirectory.file("${ext.get("name")}-${project.version}.jar"))
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
