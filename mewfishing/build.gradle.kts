plugins {
    id("cc.mewcraft.base")
    id("net.kyori.indra") version "3.0.1"
    id("io.papermc.paperweight.userdev")
}

group = "cc.mewcraft"
description = "Let's customize fishing!"
version = "1.13.1"

dependencies {
    // NMS
    paperweight.paperDevBundle("1.19.4-R0.1-SNAPSHOT")

    // Server API
    compileOnly("org.purpurmc.purpur", "purpur-api", "1.19.4-R0.1-SNAPSHOT")

    // Plugin libs
    compileOnly(project(":mewcore"))
    compileOnly("me.lucko", "helper", "5.6.13")

    // 3rd party plugins
    compileOnly("net.luckperms", "api", "5.4")
    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7") { isTransitive = false }
    compileOnly("com.palmergames.bukkit.towny", "towny", "0.99.0.6")
    compileOnly("me.xanium.gemseconomy", "GemsEconomy", "1.3.8")

    // Test
    testImplementation("io.papermc.paper", "paper-api", "1.19.4-R0.1-SNAPSHOT")
    testImplementation("org.junit.jupiter", "junit-jupiter", "5.8.2")
    testImplementation("me.lucko", "helper", "5.6.13")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
    reobfJar {
        outputJar.set(layout.buildDirectory.file("MewFishing-${project.version}.jar"))
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
                commandLine("rsync", reobfJar.get().outputJar.get().asFile.absoluteFile, "dev:data/dev/jar")
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
