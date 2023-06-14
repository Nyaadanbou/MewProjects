plugins {
    `java-library`
    id("com.github.johnrengelman.shadow")
}

tasks {
    val inputJar = lazy { shadowJar.get().archiveFile.get().asFile.absolutePath }
    val outputName = lazy { "${ext.get("name")}-${project.version}.jar" }
    val outputJar = lazy { layout.buildDirectory.file(outputName.value).get().asFile.absolutePath }

    build {
        finalizedBy("copyJar")
    }
    register<Copy>("copyJar") {
        from(inputJar.value)
        into(layout.buildDirectory)
        rename("${project.name}.*\\.jar", outputName.value)
    }
    register("deployJar") {
        doLast {
            exec {
                commandLine("rsync", outputJar.value, "dev:data/dev/jar")
            }
        }
    }
    register("deployJarFresh") {
        dependsOn(build)
    }
}
