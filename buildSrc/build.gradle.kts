plugins {
    `kotlin-dsl`
    `maven-publish`
}

group = "cc.mewcraft.conventions"
version = "1.0.0"

extra["user_home"] = when {
    System.getProperty("os.name").startsWith("Windows", ignoreCase = true) -> System.getenv("USERPROFILE")
    else -> System.getenv("HOME")
}

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
        maven {
            // publish my gradle conventions
            url = uri("${extra["user_home"]}MewcraftRepository")
        }
    }
}
