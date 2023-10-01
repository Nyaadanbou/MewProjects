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
    val pluginSuffix = "gradle.plugin"

    implementation("net.kyori.indra", "net.kyori.indra.$pluginSuffix", "3.1.1")
    implementation("com.github.johnrengelman.shadow", "com.github.johnrengelman.shadow.$pluginSuffix", "8.1.1")

    val kotlinVersion = "1.9.10"
    implementation("org.jetbrains.kotlin.jvm", "org.jetbrains.kotlin.jvm.$pluginSuffix", kotlinVersion)
    implementation("org.jetbrains.kotlin.plugin.serialization", "org.jetbrains.kotlin.plugin.serialization.$pluginSuffix", kotlinVersion)
    implementation("org.jetbrains.kotlin.plugin.atomicfu", "org.jetbrains.kotlin.plugin.atomicfu.$pluginSuffix", kotlinVersion)

    // TODO Use plugin-yml for all projects
    // implementation("net.minecrell.plugin-yml.paper", "net.minecrell.plugin-yml.paper.$pluginSuffix", "0.6.0")
}

// Allows to publish my convention plugins which may be used by my other projects
publishing {
    repositories {
        maven {
            url = uri("${System.getenv("HOME")}/MewcraftRepository")
        }
    }
}
