plugins {
    id("cc.mewcraft.base")
    id("net.kyori.indra") version "3.0.1"
}

group = "cc.mewcraft"
version = "1.1.0"
description = "Allows to create custom help messages (with MiniMessage support)"

dependencies {
    compileOnly(project(":mewcore"))
    compileOnly("io.papermc.paper", "paper-api", "1.19.4-R0.1-SNAPSHOT")
    compileOnly("me.lucko", "helper", "5.6.13")
}

tasks {
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
    register("deployToServer") {
        dependsOn(build)
        doLast {
            exec {
                commandLine("rsync", jar.get().archiveFile.get().asFile.path, "dev:data/dev/jar")
            }
        }
    }
}

indra {
    javaVersions().target(17)
}
