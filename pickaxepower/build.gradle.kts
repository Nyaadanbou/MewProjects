plugins {
    id("cc.mewcraft.base")
    id("net.kyori.indra") version "3.0.1"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "cc.mewcraft"
version = "1.0.0"
description = "Add pickaxe power system to your pickaxes!"

dependencies {
    compileOnly(project(":mewcore"))

    compileOnly("org.purpurmc.purpur", "purpur-api", "1.19.4-R0.1-SNAPSHOT")

    // 3rd party plugins
    compileOnly("me.lucko", "helper", "5.6.13") { isTransitive = false }
    compileOnly("com.comphenix.protocol", "ProtocolLib", "5.0.0")
    compileOnly("com.github.LoneDev6", "api-itemsadder", "3.4.1-r4")
}

tasks {
    jar {
        archiveBaseName.set("PickaxePower")
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
