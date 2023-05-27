plugins {
    id("cc.mewcraft.base")
    id("net.kyori.indra") version "3.0.1"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "cc.mewcraft.adventurelevel"
version = "1.0.0"
description = "Add adventure level to players"

dependencies {
    compileOnly(project(":mewcore"))
    compileOnly("org.purpurmc.purpur", "purpur-api", "1.19.4-R0.1-SNAPSHOT")

    // Libs to shade
    implementation("com.ezylang", "EvalEx", "3.0.4")

    // 3rd party plugins
    compileOnly("me.lucko", "helper", "5.6.13") { isTransitive = false }
    compileOnly("io.github.miniplaceholders", "miniplaceholders-api", "2.1.0")
    compileOnly("me.clip", "placeholderapi", "2.11.2") { isTransitive = false }
    compileOnly("net.essentialsx", "EssentialsX", "2.19.0") { isTransitive = false }
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }
    jar {
        archiveClassifier.set("noshade")
    }
    shadowJar {
        archiveBaseName.set("AdventureLevel")
        archiveClassifier.set("")
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
