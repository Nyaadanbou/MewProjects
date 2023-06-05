plugins {
    alias(libs.plugins.shadow)
}

dependencies {
    runtimeOnly(project(":proxychatbridge:common"))
    runtimeOnly(project(":proxychatbridge:bukkit"))
    runtimeOnly(project(":proxychatbridge:velocity"))
    runtimeOnly(libs.adventure.binaryserializer) {
        exclude("net.kyori")
    }
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }
    shadowJar {
        archiveFileName.set("ProxyChatBridge-${project.version}.jar")
        relocate("net.gauntletmc.adventure.serializer.binary", "com.ranull.proxychatbridge.adventure.serializer.binary")
    }
    jar {
        archiveClassifier.set("noshade")
    }
    register("deployJar") {
        doLast {
            exec {
                commandLine("rsync", shadowJar.get().archiveFile.get().asFile.absolutePath, "dev:data/dev/jar")
                commandLine("rsync", shadowJar.get().archiveFile.get().asFile.absolutePath.lowercase(), "dev:data/dev/jar")
            }
        }
    }
    register("deployJarFresh") {
        dependsOn(build)
        finalizedBy(named("deployJar"))
    }
}
