plugins {
    `kotlin-dsl`
    `maven-publish`
}

group = "cc.mewcraft.conventions"
version = "1.0.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("net.kyori.indra", "net.kyori.indra.gradle.plugin", "3.1.+")
    implementation("com.github.johnrengelman.shadow", "com.github.johnrengelman.shadow.gradle.plugin", "8.1.+")
    val kotlinVersion = "1.9.10"
    implementation("org.jetbrains.kotlin.jvm", "org.jetbrains.kotlin.jvm.gradle.plugin", kotlinVersion)
    implementation("org.jetbrains.kotlin.plugin.serialization", "org.jetbrains.kotlin.plugin.serialization.gradle.plugin", kotlinVersion)
    implementation("org.jetbrains.kotlin.plugin.atomicfu", "org.jetbrains.kotlin.plugin.atomicfu.gradle.plugin", kotlinVersion)
}

// Allows to publish my convention plugins which may be used by my other projects
publishing {
    repositories {
        maven {
            url = uri("${System.getenv("HOME")}/MewcraftRepository")
        }
    }
}
