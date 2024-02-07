plugins {
    kotlin("jvm")
}

dependencies {
    implementation("com.google.inject", "guice", Versions.Guice) {
        exclude("com.google.guava") // provided in paper runtime
    }
    implementation("com.google.inject.extensions", "guice-assistedinject", Versions.GuiceAssisted) {
        exclude("com.google.guava") // provided in paper runtime
    }
}
