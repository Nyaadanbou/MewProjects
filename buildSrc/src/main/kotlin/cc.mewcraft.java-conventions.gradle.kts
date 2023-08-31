import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    kotlin("jvm")
    id("net.kyori.indra")
    id("com.github.johnrengelman.shadow")
}

dependencies {
    // for better compile-time checking
    compileOnly("org.jetbrains", "annotations", "24.0.1")
    compileOnly("org.checkerframework", "checker-qual", "3.28.0")
    compileOnly("org.apiguardian", "apiguardian-api", "1.1.2")
}

tasks {
    compileJava {
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

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "17"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "17"
}