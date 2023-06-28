plugins {
    `java-library`
    id("com.github.johnrengelman.shadow")
}

tasks {
    val inputJarPath = lazy { shadowJar.get().archiveFile.get().asFile.absolutePath }
    val finalJarName = lazy { "${ext.get("name")}-${project.version}.jar" }
    val finalJarPath = lazy { layout.buildDirectory.file(finalJarName.value).get().asFile.absolutePath }

    build {
        finalizedBy("copyJar")
    }
    register<Copy>("copyJar") {
        from(inputJarPath.value)
        into(layout.buildDirectory)
        rename("(?i)${project.name}.*\\.jar", finalJarName.value)
    }
    register<Task>("deployJar") {
        doLast {
            exec {
                commandLine("rsync", finalJarPath.value, "dev:data/dev/jar")
            }
        }
    }
    register<Task>("deployJarFresh") {
        dependsOn(build)
        finalizedBy(named("deployJar"))
    }
}
