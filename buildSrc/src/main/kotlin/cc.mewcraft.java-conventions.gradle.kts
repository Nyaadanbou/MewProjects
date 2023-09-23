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
    assemble {
        dependsOn(shadowJar)
    }
    shadowJar {
        archiveClassifier.set("shaded")
    }
}

java {
    withSourcesJar()
}

indra {
    javaVersions().target(17)
}
