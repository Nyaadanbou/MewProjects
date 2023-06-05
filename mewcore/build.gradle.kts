plugins {
    id("cc.mewcraft.java-conventions")
    id("cc.mewcraft.repository-conventions")
    id("cc.mewcraft.publishing-conventions")
    alias(libs.plugins.indra)
    alias(libs.plugins.shadow)
}

group = "cc.mewcraft"
version = "5.17.0"
description = "Common code of all Mewcraft plugins."

// Reference: https://youtrack.jetbrains.com/issue/IDEA-276365
configurations.compileOnlyApi {
    isCanBeResolved = true
}

dependencies {
    // Shaded libs - these will be loaded by my other plugins
    compileOnlyApi(libs.guice)
    compileOnlyApi(libs.hikari)
    compileOnlyApi(libs.anvilgui)
    compileOnlyApi(libs.bundles.cmds) {
        exclude("net.kyori")
    }
    compileOnlyApi(libs.configurate)
    compileOnlyApi(libs.lang.bukkit)
    compileOnlyApi(libs.lang.velocity)
    compileOnlyApi(libs.authlib) {
        exclude("com.google.guava")
        exclude("com.google.code.gson")
        exclude("com.google.code.findbugs")
        exclude("org.apache.logging.log4j")
    }

    // Plugin libs - these will be present as other plugins
    compileOnly(libs.helper)

    // The Minecraft server API
    compileOnly(libs.server.paper)

    // Other random plugins that we may interact with
    compileOnly(libs.luckperms)
    compileOnly(libs.vault) { isTransitive = false }
    compileOnly(libs.essentials) { isTransitive = false }
    compileOnly(libs.towny)
    compileOnly(libs.itemsadder)
    compileOnly(libs.mythiclib)
    compileOnly(libs.mmoitems)
    compileOnly(libs.interactivebooks)
    compileOnly(libs.brewery) { isTransitive = false }
    compileOnly(libs.papi) { isTransitive = false }
    compileOnly(libs.minipapi) { isTransitive = false }

    testImplementation(libs.server.paper)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.helper)
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
        /*val path = "cc.mewcraft.lib."
        relocate("com.mojang.authlib", path + "authlib")
        relocate("com.mojang.util", path + "authlib.util")
        relocate("org.apache.commons", path + "apache.commons")

        relocate("cloud.commandframework", path + "commandframework")

        relocate("org.spongepowered.configurate", path + "configurate")
        relocate("org.yaml.snakeyaml", path + "snakeyaml")
        relocate("io.leangen.geantyref", path + "geantyref") // shared by "configurate" and "commandfrmaework"

        relocate("de.themoep.utils.lang", path + "lang")*/
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

/*publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}*/
