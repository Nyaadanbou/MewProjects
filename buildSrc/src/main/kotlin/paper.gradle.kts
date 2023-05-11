plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenLocal {
        content {
            includeGroup("cc.mewcraft")
        }
    }
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        content {
            includeGroup("io.papermc.paper")
            includeGroup("net.md-5")
            includeGroup("com.mojang")
        }
    }
    maven("https://jitpack.io") {
        content {
            includeGroup("com.github.MilkBowl")
        }
    }
    maven("https://mvn.lumine.io/repository/maven-public/") {
        content {
            includeGroup("io.lumine")
        }
    }
    maven("https://maven.enginehub.org/repo/") {
        content {
            includeGroup("com.sk89q.worldguard")
            includeGroup("com.sk89q.worldguard.worldguard-libs")
            includeGroup("com.sk89q.worldedit")
            includeGroup("com.sk89q.worldedit.worldedit-libs")
        }
    }
    maven("https://repo.citizensnpcs.co/#/") {
        content {
            includeGroup("net.citizensnpcs")
        }
    }
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") {
        content {
            includeGroup("me.clip")
        }
    }
    maven("https://repo.opencollab.dev/maven-snapshots/") {
        content {
            includeGroup("org.geysermc.floodgate")
        }
    }
}

dependencies {
    implementation("org.jetbrains:annotations:24.0.0")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
}