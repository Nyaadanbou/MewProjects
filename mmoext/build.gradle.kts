plugins {
    `java-library`
    `maven-publish`

    val indraVersion = "3.0.1"
    id("net.kyori.indra") version indraVersion
    id("net.kyori.indra.git") version indraVersion

    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "cc.mewcraft"
version = "2.7".decorateVersion()
description = "Common code of all Mewcraft plugins."

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.purpurmc.org/snapshots")
    maven("https://repo.lucko.me")
    maven("https://repo.minebench.de/")
    maven("https://mvn.lumine.io/repository/maven-public/")
}

dependencies {
    // API
    compileOnly("org.purpurmc.purpur:purpur-api:1.19.4-R0.1-SNAPSHOT")

    // Plugin library
    compileOnly(project(":mewcore"))
    compileOnly("me.lucko:helper:5.6.13")

    // External plugins
    compileOnly("com.github.LoneDev6:api-itemsadder:3.0.0")
    compileOnly("io.lumine:Mythic-Dist:5.2.6")
    compileOnly("io.lumine:MythicLib-dist:1.6-SNAPSHOT")
    compileOnly("net.Indyuce:MMOItems-API:6.9.4-SNAPSHOT")
    compileOnly("net.milkbowl.vault:VaultAPI:1.7") { isTransitive = false }
    compileOnly("com.github.TownyAdvanced:Towny:0.99.0.0")
}

tasks {
    processResources {
        filesMatching("**/paper-plugin.yml") {
            expand(mapOf(
                "version" to "${project.version}",
                "description" to project.description
            ))
        }
    }
    register("deployJar") {
        doLast {
            exec {
                commandLine("rsync", shadowJar.get().archiveFile.get().asFile.absoluteFile, "dev:data/dev/jar")
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

fun lastCommitHash(): String = indraGit.commit()?.name?.substring(0, 7) ?: error("Could not determine commit hash")
fun String.decorateVersion(): String = if (endsWith("-SNAPSHOT")) "$this-${lastCommitHash()}" else this