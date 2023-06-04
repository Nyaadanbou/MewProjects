plugins {
    id("cc.mewcraft.java-conventions")
    id("cc.mewcraft.repository-conventions")
    alias(libs.plugins.shadow)
}

group = "cc.mewcraft"
version = "1.22.1"
description = "A plugin consisting of many small features"

dependencies {
    implementation(project(":mewutils:base"))

    // 3rd party plugins
    compileOnly(libs.luckperms)
    compileOnly(libs.vault) { isTransitive = false }
    compileOnly(libs.papi) { isTransitive = false }
    compileOnly(libs.itemsadder)
    compileOnly(libs.protocollib)
    compileOnly(libs.essentials) { isTransitive = false }
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
