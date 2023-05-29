plugins {
    `kotlin-dsl`
    `maven-publish`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

group = "cc.mewcraft.conventions"
version = "1.0.0"

// it allows to publish my convention plugins
// which may be used by my other projects
publishing {
    repositories {
        maven {
            url = uri("${System.getenv("HOME")}/MewcraftRepository")
        }
    }
}
