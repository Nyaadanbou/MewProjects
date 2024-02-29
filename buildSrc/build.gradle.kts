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
    implementation(libs.indra.common)
    implementation(libs.shadow)
    implementation(libs.kotlin.jvm)
    implementation(libs.kotlin.plugin.serialization)
    implementation(libs.kotlin.plugin.atomicfu)
    implementation(libs.ksp)
}

publishing {
    repositories {
        maven(uri("${System.getProperty("user.home")}/MewcraftRepository"))
    }
}
