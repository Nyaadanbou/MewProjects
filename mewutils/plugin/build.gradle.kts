plugins {
    id("cc.mewcraft.base")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "cc.mewcraft"
version = "1.21.0"
description = "A plugin consisting of many small features"

dependencies {
    implementation(project(":mewutils:base"))
    compileOnly("net.wesjd", "anvilgui", "1.6.3-SNAPSHOT")

    // 3rd party plugins
    compileOnly("net.luckperms", "api", "5.4")
    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7") { isTransitive = false }
    compileOnly("me.clip", "placeholderapi", "2.11.2") { isTransitive = false }
    compileOnly("com.github.LoneDev6", "api-itemsadder", "3.4.1-r4")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.0.0-SNAPSHOT")
    compileOnly("net.essentialsx", "EssentialsX", "2.19.0") { isTransitive = false }
}

tasks {
    jar {
        archiveClassifier.set("nonshade")
    }
    assemble {
        dependsOn(shadowJar)
    }
    shadowJar {
        // Shadow is only used to include classes from other modules
        archiveFileName.set("MewUtils-${project.version}.jar")
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
