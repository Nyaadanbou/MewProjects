plugins {
    `java-library`

    id("net.kyori.indra")
    id("com.github.johnrengelman.shadow")
}

dependencies {
    // These dependencies are for better compile-time check.
    compileOnly("org.jetbrains", "annotations", "24.0.0")
    compileOnly("org.checkerframework", "checker-qual", "3.28.0")
}

tasks {
    compileJava {
        dependsOn(clean)
    }
    compileTestJava {
        dependsOn(clean)
    }

    // Configure shadow
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

    // We do not need javadoc, source is enough and better
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
    // See: https://github.com/KyoriPowered/indra/wiki/indra
    javaVersions().target(17)
}
