plugins {
    id("cc.mewcraft.java-conventions")
    id("cc.mewcraft.repository-conventions")
    alias(libs.plugins.indra)
}

group = "cc.mewcraft"
version = "1.0.0"
description = "Makes players log back in to the server they logged out from"

dependencies {
    compileOnly(libs.proxy.velocity)
    annotationProcessor(libs.proxy.velocity)
}

tasks {
    jar {
        archiveBaseName.set("persistentserver")
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
