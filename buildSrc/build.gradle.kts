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
    implementation("net.kyori.indra", "net.kyori.indra.gradle.plugin", "3.1.1")
    implementation("com.github.johnrengelman.shadow", "com.github.johnrengelman.shadow.gradle.plugin", "8.1.1")
    // implementation("net.minecrell.plugin-yml.paper", "net.minecrell.plugin-yml.paper.gradle.plugin", "0.6.0")
}

// it allows to publish my convention plugins
// which may be used by my other projects
publishing {
    repositories {
        maven {
            url = uri("${System.getenv("HOME")}/MewcraftRepository")
        }
    }
}
