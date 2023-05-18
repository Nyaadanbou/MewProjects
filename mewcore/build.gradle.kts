plugins {
    id("cc.mewcraft.base")
    id("net.kyori.indra") version "3.0.1"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "cc.mewcraft"
version = "5.16.1"
description = "Common code of all Mewcraft plugins."

// Reference: https://youtrack.jetbrains.com/issue/IDEA-276365
configurations.compileOnlyApi {
    isCanBeResolved = true
}

dependencies {
    // Shaded libs - these will be loaded by my other plugins
    compileOnlyApi("com.google.inject", "guice", "5.1.0")
    val cloudVersion = "1.8.3"
    compileOnlyApi("cloud.commandframework", "cloud-paper", cloudVersion)
    compileOnlyApi("cloud.commandframework", "cloud-minecraft-extras", cloudVersion) {
        exclude("net.kyori")
    }
    compileOnlyApi("org.spongepowered", "configurate-yaml", "4.1.2")
    compileOnlyApi("de.themoep.utils", "lang-bukkit", "1.3-SNAPSHOT")
    compileOnlyApi("com.mojang", "authlib", "1.5.25") {
        exclude("com.google.guava")
        exclude("com.google.code.gson")
        exclude("com.google.code.findbugs")
        exclude("org.apache.logging.log4j")
    }

    // Plugin libs - these will be present as other plugins
    api("me.lucko", "helper", "5.6.13")

    // Server API
    api("org.purpurmc.purpur", "purpur-api", "1.19.4-R0.1-SNAPSHOT")

    // For better compile-time checking
    compileOnlyApi("org.checkerframework", "checker-qual", "3.28.0")
    compileOnlyApi("org.apiguardian", "apiguardian-api", "1.1.2")

    // 3rd party plugins
    compileOnly("me.lucko", "helper", "5.6.13") { isTransitive = false }
    compileOnly("net.luckperms", "api", "5.4")
    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7") { isTransitive = false }
    compileOnly("net.essentialsx", "EssentialsX", "2.19.0") { isTransitive = false }
    compileOnly("com.palmergames.bukkit.towny", "towny", "0.99.0.6")
    compileOnly("com.github.LoneDev6", "api-itemsadder", "3.4.1-r4")
    compileOnly("io.lumine", "MythicLib-dist", "1.6-SNAPSHOT")
    compileOnly("net.Indyuce", "MMOItems-API", "6.9.4-SNAPSHOT")
    compileOnly("net.leonardo_dgs", "InteractiveBooks", "1.6.5")
    compileOnly("com.github.DieReicheErethons", "Brewery", "3.1.1") { isTransitive = false }

    testImplementation("io.papermc.paper", "paper-api", "1.19.4-R0.1-SNAPSHOT")
    testImplementation("org.junit.jupiter", "junit-jupiter", "5.9.0")
    testImplementation("me.lucko", "helper", "5.6.13")
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }
    shadowJar {
        archiveBaseName.set("MewCore")
        archiveClassifier.set("shaded")
        configurations = listOf(project.configurations.compileOnlyApi.get())

        // Paper Plugins have isolated classloaders so relocating classes is no longer needed
        // val path = "cc.mewcraft.lib."
        // relocate("com.mojang.authlib", path + "authlib")
        // relocate("com.mojang.util", path + "authlib.util")
        // relocate("org.apache.commons", path + "apache.commons")
        //
        // relocate("cloud.commandframework", path + "commandframework")
        //
        // relocate("org.spongepowered.configurate", path + "configurate")
        // relocate("org.yaml.snakeyaml", path + "snakeyaml")
        // relocate("io.leangen.geantyref", path + "geantyref") // shared by "configurate" and "commandfrmaework"
        //
        // relocate("de.themoep.utils.lang", path + "lang")
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
