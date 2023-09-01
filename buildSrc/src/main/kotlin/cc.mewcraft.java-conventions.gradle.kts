plugins {
    `java-library`
    kotlin("jvm")
    id("net.kyori.indra")
    id("com.github.johnrengelman.shadow")
}

dependencies {
    // These dependencies are for better compile-time checking.
    compileOnly("org.jetbrains", "annotations", "24.0.1")
    compileOnly("org.checkerframework", "checker-qual", "3.28.0")
    compileOnly("org.apiguardian", "apiguardian-api", "1.1.2")
}

tasks {
    configureTasks(compileJava, compileTestJava) {
        dependsOn(clean)
    }
    configureTasks(compileKotlin, compileTestKotlin) {
        kotlinOptions {
            jvmTarget = "17"
        }
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

fun <T : Task> configureTasks(vararg taskProviders: TaskProvider<T>, action: Action<T>) {
    taskProviders.forEach { it.configure(action) }
}
