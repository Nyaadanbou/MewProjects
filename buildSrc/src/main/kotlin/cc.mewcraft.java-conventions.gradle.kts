plugins {
    `java-library`
}

dependencies {
    // for better compile-time checking
    compileOnly("org.jetbrains", "annotations", "24.0.1")
    compileOnly("org.checkerframework", "checker-qual", "3.28.0")
    compileOnly("org.apiguardian", "apiguardian-api", "1.1.2")
}

java {
    withSourcesJar()
}