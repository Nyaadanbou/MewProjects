plugins {
    id("cc.mewcraft.java-conventions")
    id("cc.mewcraft.repository-conventions")
    id("cc.mewcraft.publishing-conventions")
    alias(libs.plugins.indra)
    alias(libs.plugins.shadow)
}

group = project(":adventurelevel:api").group
version = project(":adventurelevel:api").version
description = project(":adventurelevel:api").description

dependencies {
    // api module
    implementation(project(":adventurelevel:api"))
    implementation(project(":adventurelevel:hooks"))

    // my own libs
    compileOnly(project(":mewcore"))

    // server api
    compileOnly(libs.server.paper)

    // libs to shade into the jar
    implementation(libs.evalex)

    // libs that present as other plugins
    compileOnly(libs.helper) { isTransitive = false }
    compileOnly(libs.helper.redis)
    compileOnly(libs.essentials) { isTransitive = false }
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }
    jar {
        archiveClassifier.set("noshade")
    }
    shadowJar {
        archiveBaseName.set("AdventureLevel")
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

/*publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}*/
