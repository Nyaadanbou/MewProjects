plugins {
    id("cc.mewcraft.base")
    alias(libs.plugins.indra)
}

group = "cc.mewcraft"
version = "1.1.0"
description = "Allows to create custom help messages (with MiniMessage support)"

dependencies {
    compileOnly(project(":mewcore"))
    compileOnly(libs.server.paper)
    compileOnly(libs.helper)
}

tasks {
    jar {
        archiveBaseName.set("MewHelp")
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
    register("deployJarFresh") {
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
