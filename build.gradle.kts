plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
    val indraVersion = "3.0.1"
    id("net.kyori.indra") version indraVersion
    id("net.kyori.indra.git") version indraVersion
}

group = "cc.mewcraft"
version = "5.12".decorateVersion()
description = "Contains common code of all Mewcraft plugins."

fun lastCommitHash(): String = indraGit.commit()?.name?.substring(0, 7) ?: error("Could not determine commit hash")
fun String.decorateVersion(): String = if (endsWith("-SNAPSHOT")) "$this-${lastCommitHash()}" else this

repositories {
    mavenCentral()

    mavenLocal {
        content {
            includeGroup("net.leonardo_dgs")
            includeGroup("com.github.DieReicheErethons")
            includeGroup("de.themoep.utils") // remote is down
            includeGroup("net.Indyuce") // remote is down
        }
    }
    maven("https://papermc.io/repo/repository/maven-public/") {
        content {
            includeGroup("io.papermc.paper")
            includeGroup("net.md-5")
            includeGroup("com.mojang")
        }
    }
    // maven("https://repo.minebench.de/") {
    //     content {
    //         includeGroup("de.themoep.utils")
    //     }
    // }
    maven("https://repo.lucko.me") {
        content {
            includeGroup("me.lucko")
        }
    }
    maven("https://jitpack.io") {
        content {
            includeGroup("com.github.TownyAdvanced")
            includeGroup("com.github.MilkBowl")
            includeGroup("com.github.LoneDev6")
        }
    }
    maven("https://repo.essentialsx.net/releases/") {
        mavenContent {
            includeGroup("net.essentialsx")
        }
    }
    maven("https://nexus.phoenixdevt.fr/repository/maven-public/") {
        mavenContent {
            includeGroup("io.lumine")
        }
    }
}

dependencies {
    // Shaded libs to share with other plugins
    val cloudVersion = "1.8.0"
    implementation("cloud.commandframework", "cloud-paper", cloudVersion)
    implementation("cloud.commandframework", "cloud-minecraft-extras", cloudVersion) {
        exclude("net.kyori")
    }
    implementation("org.spongepowered", "configurate-yaml", "4.1.2")
    implementation("de.themoep.utils", "lang-bukkit", "1.3-SNAPSHOT")
    implementation("com.mojang", "authlib", "1.5.25") {
        exclude("com.google.guava")
        exclude("com.google.code.gson")
        exclude("com.google.code.findbugs")
        exclude("org.apache.logging.log4j")
    }

    // Server API
    compileOnly("io.papermc.paper", "paper-api", "1.19.3-R0.1-SNAPSHOT")

    // Better compile time check
    compileOnlyApi("org.checkerframework", "checker-qual", "3.28.0")
    compileOnlyApi("org.apiguardian", "apiguardian-api", "1.1.2")

    // 3rd party plugins
    compileOnly("me.lucko", "helper", "5.6.10") {
        isTransitive = false
    }
    compileOnly("net.luckperms", "api", "5.4")
    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7") {
        isTransitive = false
    }
    compileOnly("net.essentialsx", "EssentialsX", "2.19.0") {
        isTransitive = false
    }
    compileOnly("com.github.TownyAdvanced", "Towny", "0.98.3.0")
    compileOnly("com.github.LoneDev6", "api-itemsadder", "3.0.0")
    compileOnly("io.lumine", "MythicLib-dist", "1.5.1-SNAPSHOT")
    compileOnly("net.Indyuce", "MMOItems-API", "6.9.2-SNAPSHOT")
    compileOnly("net.leonardo_dgs", "InteractiveBooks", "1.6.3")
    compileOnly("com.github.DieReicheErethons", "Brewery", "3.1.1") {
        isTransitive = false
    }

    testImplementation("io.papermc.paper", "paper-api", "1.19.3-R0.1-SNAPSHOT")
    testImplementation("org.junit.jupiter", "junit-jupiter", "5.9.0")
    testImplementation("me.lucko", "helper", "5.6.13")
}

bukkit {
    main = "cc.mewcraft.mewcore.MewCore"
    name = project.name
    version = "${project.version}"
    description = project.description
    apiVersion = "1.17"
    authors = listOf("Nailm")
    depend = listOf("helper")
    softDepend = listOf(
        "Vault",
        "LuckPerms",
        "Towny",
        "PlaceholderAPI",
        "ItemsAdder",
        "MythicLib",
        "MMOItems",
        "InteractiveBooks",
        "Brewery"
    )
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }
    shadowJar {
        val path = "cc.mewcraft.lib."

        relocate("com.mojang.authlib", path + "authlib")
        relocate("com.mojang.util", path + "authlib.util")
        relocate("org.apache.commons", path + "apache.commons")

        relocate("cloud.commandframework", path + "commandframework")

        relocate("org.spongepowered.configurate", path + "configurate")
        relocate("org.yaml.snakeyaml", path + "snakeyaml")
        relocate("io.leangen.geantyref", path + "geantyref") // shared by "configurate" and "commandfrmaework"

        relocate("de.themoep.utils.lang", path + "lang")

        archiveFileName.set("MewCore-${project.version}.jar")
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

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
