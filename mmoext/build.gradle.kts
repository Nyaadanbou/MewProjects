plugins {
    id("cc.mewcraft.base")
    id("net.kyori.indra") version "3.0.1"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "cc.mewcraft"
version = "2.8.0"
description = "An extension of MMO plugins."

dependencies {
    // API
    compileOnly("org.purpurmc.purpur", "purpur-api", "1.19.4-R0.1-SNAPSHOT")

    // Plugin library
    compileOnly(project(":mewcore"))
    compileOnly("me.lucko", "helper", "5.6.13")

    // External plugins
    compileOnly("com.github.LoneDev6", "api-itemsadder", "3.4.1-r4")
    compileOnly("io.lumine", "Mythic-Dist", "5.2.6")
    compileOnly("io.lumine", "MythicLib-dist", "1.6-SNAPSHOT")
    compileOnly("net.Indyuce", "MMOItems-API", "6.9.4-SNAPSHOT")
    compileOnly("com.palmergames.bukkit.towny", "towny", "0.99.0.0")
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
