plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    kotlin("plugin.atomicfu")

    id("com.github.johnrengelman.shadow")
}

tasks {
    // Kotlin source files are always UTF-8 by design.

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
    }
}

kotlin {
    jvmToolchain(17)

    sourceSets {
        val main by getting {
            dependencies {
                // All kotlin dependencies are shipped with: https://github.com/GamerCoder215/KotlinMC
                // So we can just declare all these kotlin dependencies as `compileOnly`.
                compileOnly(kotlin("stdlib"))
                compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.0")
                compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
                compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-hocon:1.6.0")
                compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                compileOnly("org.jetbrains.kotlinx:kotlinx-io-core:0.3.0")
                compileOnly("org.jetbrains.kotlinx:atomicfu:0.22.0")
            }
        }
        val test by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
