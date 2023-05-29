plugins {
    id("cc.mewcraft.java-conventions")
    id("cc.mewcraft.repository-conventions")
    alias(libs.plugins.indra)
    alias(libs.plugins.shadow)
}

group = "cc.mewcraft"
version = "2.8.0"
description = "An extension of MMO plugins."

dependencies {
    // API
    compileOnly(libs.server.paper)

    // Plugin library
    compileOnly(project(":mewcore"))
    compileOnly(libs.helper)

    // External plugins
    compileOnly(libs.itemsadder)
    compileOnly(libs.mythicmobs)
    compileOnly(libs.mythiclib)
    compileOnly(libs.mmoitems)
    compileOnly(libs.towny)
}

tasks {
    jar {
        archiveBaseName.set("MMOExt")
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
                commandLine("rsync", jar.get().archiveFile.get().asFile.absoluteFile, "dev:data/dev/jar")
            }
        }
    }
    register("deployJarFresh") {
        dependsOn(build)
        finalizedBy(named("deployJar"))
    }
}

indra {
    javaVersions().target(17)
}
