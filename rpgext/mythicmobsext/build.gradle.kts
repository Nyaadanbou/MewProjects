plugins {
    alias(libs.plugins.indra)
}

group = "cc.mewcraft.mythicmobsext"
version = "1.0.0"
description = "An extension of MythicMobs plugin"

dependencies {
    // server api
    compileOnly(libs.server.paper)

    // dependent module
    compileOnly(project(":rpgext:common"))

    // libs that present as other plugins
    compileOnly(project(":mewcore"))
    compileOnly(libs.helper)
    compileOnly(libs.mmoitems)
    compileOnly(libs.mythiclib)
    compileOnly(libs.mythicmobs)
}

tasks {
    jar {
        archiveBaseName.set("MythicMobsExt")
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
