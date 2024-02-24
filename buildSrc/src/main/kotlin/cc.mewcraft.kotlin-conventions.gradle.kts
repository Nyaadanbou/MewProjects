@file:Suppress("UNUSED_VARIABLE")

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    kotlin("plugin.atomicfu")
    id("com.github.johnrengelman.shadow")
}

tasks {
    compileKotlin {
        dependsOn(clean)
    }
    compileTestKotlin {
        dependsOn(clean)
    }
    assemble {
        dependsOn(shadowJar)
    }
    shadowJar {
        archiveClassifier.set("shaded")
        dependencies {
            exclude("META-INF/NOTICE")
            exclude("META-INF/maven/**")
            exclude("META-INF/versions/**")
            exclude("META-INF/**.kotlin_module")
        }
    }
    test {
        jvmArgs("-XX:+EnableDynamicAgentLoading") // surpress Java agent warning
        useJUnitPlatform() // use JUnit 5
    }
}

java {
    withSourcesJar()
}

kotlin {
    jvmToolchain(21)

    sourceSets {
        val main by getting {
            dependencies {
                // All kotlin dependencies are shipped with: https://github.com/GamerCoder215/KotlinMC
                // So we can just declare all these kotlin dependencies as `compileOnly`.
                compileOnly(kotlin("stdlib"))
                compileOnly("org.jetbrains.kotlin:kotlin-reflect:${Versions.Kotlin}")
                compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-core:${Versions.KotlinxSerialization}")
                compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.KotlinxSerialization}")
                compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-hocon:${Versions.KotlinxSerialization}")
                compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.KotlinxCoroutines}")
                compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-guava:${Versions.KotlinxCoroutines}") {
                    isTransitive = false
                }
                compileOnly("org.jetbrains.kotlinx:kotlinx-io-core:${Versions.KotlinxIO}")
                compileOnly("org.jetbrains.kotlinx:atomicfu:${Versions.KotlinxAtomicfu}")
            }
        }
        val test by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlin:kotlin-reflect:${Versions.Kotlin}")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:${Versions.KotlinxSerialization}")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.KotlinxSerialization}")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-hocon:${Versions.KotlinxSerialization}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.KotlinxCoroutines}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:${Versions.KotlinxCoroutines}")
                implementation("org.jetbrains.kotlinx:kotlinx-io-core:${Versions.KotlinxIO}")
                implementation("org.jetbrains.kotlinx:atomicfu:${Versions.KotlinxAtomicfu}")
            }
        }
    }
}
