plugins {
    `java-library`
    id("com.google.devtools.ksp")
}

sourceSets.main {
    java.srcDirs("build/generated/ksp/main/kotlin")
}

dependencies {
    implementation("io.insert-koin", "koin-core", Versions.KoinCore) {
        exclude("org.jetbrains.kotlin")
    }
    implementation("io.insert-koin", "koin-annotations", Versions.KoinAnnotations) {
        exclude("org.jetbrains.kotlin")
    }
    ksp("io.insert-koin", "koin-ksp-compiler", Versions.KoinKsp)
}
