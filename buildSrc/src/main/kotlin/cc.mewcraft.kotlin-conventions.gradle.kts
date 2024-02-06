@file:Suppress("UNUSED_VARIABLE")

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    kotlin("plugin.atomicfu")
    id("com.github.johnrengelman.shadow")
}

tasks {
    // Kotlin source files are UTF-8 by default.
    compileKotlin {
        dependsOn(clean)
    }
    compileTestKotlin {
        dependsOn(clean)
    }

    // Configure shadow
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
                compileOnly("org.jetbrains.kotlin:kotlin-reflect:1.9.22")
                compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.2")
                compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
                compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-hocon:1.6.2")
                compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.7.3") {
                    isTransitive = false
                }
                compileOnly("org.jetbrains.kotlinx:kotlinx-io-core:0.3.0")
                compileOnly("org.jetbrains.kotlinx:atomicfu:0.23.1")
            }
        }
        val test by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-hocon:1.6.0")
            }
        }
    }
}
