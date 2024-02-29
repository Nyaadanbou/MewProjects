plugins {
    `java-library`
    id("net.kyori.indra")
    id("com.github.johnrengelman.shadow")
}

dependencies {
    compileOnly("org.jetbrains", "annotations", Versions.JetbrainsAnnotations)
    compileOnly("org.checkerframework", "checker-qual", Versions.CheckframeworkQual)
}

tasks {
    compileJava {
        dependsOn(clean)
    }
    compileTestJava {
        dependsOn(clean)
    }
    assemble {
        dependsOn(shadowJar)
    }
    shadowJar {
        archiveClassifier.set("shaded")
        dependencies {
            exclude("META-INF/NOTICE")
            exclude("META-INF/LICENSE")
            exclude("META-INF/DEPENDENCIES")
            exclude("META-INF/maven/**")
            exclude("META-INF/versions/**")
            exclude("META-INF/**.kotlin_module")
        }
    }
    javadoc {
        enabled = false
    }
    javadocJar {
        enabled = false
    }
}

java {
    withSourcesJar()
}

indra {
    javaVersions().target(21)
}
