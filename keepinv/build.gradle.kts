plugins {
    id("cc.mewcraft.java-conventions")
    id("cc.mewcraft.repository-conventions")
    alias(libs.plugins.indra)
    alias(libs.plugins.shadow)
}

group = "cc.mewcraft"
version = "1.0.0"
description = "Provides special handling of the keep-inventory feature"

dependencies {
    compileOnly(project(":mewcore"))
    compileOnly(libs.server.paper)

    // 3rd party plugins
    compileOnly(libs.helper) { isTransitive = false }
    compileOnly(libs.itemsadder)
}

tasks {
    jar {
        archiveBaseName.set("KeepInv")
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