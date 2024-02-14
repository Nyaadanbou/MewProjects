plugins {
    `java-library`
    id("com.google.devtools.ksp")
}

sourceSets.test {
    java.srcDirs("build/generated/ksp/test/kotlin")
}

dependencies {
    testImplementation("io.insert-koin", "koin-test", Versions.KoinCore) {
        exclude("org.jetbrains.kotlin")
    }
    testImplementation("io.insert-koin", "koin-test-junit4", Versions.KoinCore) {
        exclude("org.jetbrains.kotlin")
    }
}
