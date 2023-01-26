plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
    id("net.kyori.indra.git") version "2.1.1"
}

group = "cc.mewcraft"
version = "5.11".decorateVersion()
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

    compileOnly("io.papermc.paper", "paper-api", "1.19.3-R0.1-SNAPSHOT")

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
    compileOnly("com.github.LoneDev6:api-itemsadder:3.0.0")
    compileOnly("io.lumine:MythicLib-dist:1.5.1-SNAPSHOT")
    compileOnly("net.Indyuce:MMOItems-API:6.9.2-SNAPSHOT")
    compileOnly("net.leonardo_dgs:InteractiveBooks:1.6.3")
    compileOnly("com.github.DieReicheErethons:Brewery:3.1.1") {
        isTransitive = false
    }

    // Test environment
    testCompileOnly("io.papermc.paper", "paper-api", "1.19.3-R0.1-SNAPSHOT")
    testCompileOnly("org.junit.jupiter", "junit-jupiter", "5.9.0")
    testCompileOnly("me.lucko", "helper", "5.6.10")
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
    val out = "MewCore-${project.version}.jar"
    build {
        dependsOn(shadowJar)
    }
    jar {
        destinationDirectory.set(file("$rootDir"))
        archiveClassifier.set("noshade")
    }
    shadowJar {
        val path = "cc.mewcraft.lib."

        relocate("com.mojang", path + "mojang")
        relocate("org.apache.commons", path + "apache.commons")

        relocate("cloud.commandframework", path + "commandframework")

        relocate("org.spongepowered.configurate", path + "configurate")
        relocate("org.yaml.snakeyaml", path + "snakeyaml")
        relocate("io.leangen.geantyref", path + "geantyref") // shared by "configurate" and "command"

        relocate("de.themoep.utils.lang", path + "lang")

        // minimize {
        //     exclude(dependency("cc.mewcraft:.*:.*"))
        // }

        destinationDirectory.set(file("$rootDir"))
        archiveFileName.set(out)
        archiveClassifier.set("")
    }

    register("deployToServer") {
        dependsOn(build)
        doLast {
            exec {
                commandLine("rsync", shadowJar.get().archiveFile.get().asFile.absoluteFile, "dev:data/dev/jar")
            }
        }
    }

    compileJava {
        options.release.set(17)
        options.encoding = Charsets.UTF_8.name()
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
}

java {
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}
