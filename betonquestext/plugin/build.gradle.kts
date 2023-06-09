plugins {
    id("io.freefair.lombok")
    alias(libs.plugins.indra)
    alias(libs.plugins.shadow)
}

version = "1.3.1"
description = "Brings BetonQuest more integrations with 3rd party plugins"

dependencies {
    implementation(project(":betonquestext:common"))

    // sub modules
    implementation(project(":betonquestext:adventurelevel"))
    implementation(project(":betonquestext:brewery"))
    implementation(project(":betonquestext:itemsadder"))

    // libs to shade into the jar
    implementation(libs.evalex)
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }
    jar {
        archiveClassifier.set("noshade")
    }
    shadowJar {
        archiveBaseName.set("BetonQuestExt")
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
