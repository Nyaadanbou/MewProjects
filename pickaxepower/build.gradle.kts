plugins {
    id("cc.mewcraft.base")
    alias(libs.plugins.indra)
    alias(libs.plugins.shadow)
}

group = "cc.mewcraft"
version = "1.0.0"
description = "Add pickaxe power system to your pickaxes!"

dependencies {
    compileOnly(project(":mewcore"))

    compileOnly(libs.server.paper)

    // 3rd party plugins
    compileOnly(libs.helper) { isTransitive = false }
    compileOnly(libs.protocollib)
    compileOnly(libs.itemsadder)
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
