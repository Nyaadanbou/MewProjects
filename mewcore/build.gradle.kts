plugins {
    id("cc.mewcraft.java-conventions")
    id("cc.mewcraft.deploy-conventions")
    id("cc.mewcraft.repository-conventions")
    id("cc.mewcraft.publishing-conventions")
    id("cc.mewcraft.paper-plugins")
}

project.ext.set("name", "MewCore")

group = "cc.mewcraft"
version = "5.17.3"
description = "Common code of all Mewcraft plugins."

// Reference: https://youtrack.jetbrains.com/issue/IDEA-276365
/*configurations {
    compileOnlyApi { isCanBeResolved = true }
    implementation { isCanBeResolved = true }
}*/

dependencies {
    // Shaded libs at compile path - these are essential for plugin development
    compileOnlyApi(libs.guice)
    compileOnlyApi(libs.bundles.cmds) {
        exclude("net.kyori")
    }
    compileOnlyApi(libs.lang.bukkit)
    compileOnlyApi(libs.authlib) {
        exclude("com.google.guava")
        exclude("com.google.code.gson")
        exclude("com.google.code.findbugs")
        exclude("org.apache.logging.log4j")
    }

    // Shaded libs at runtime path - these are optional
    // Consumers are expected to declare these dependencies
    // at [compile path] if they need these dependencies
    implementation(libs.hikari)
    implementation(libs.anvilgui)
    implementation(libs.bundles.invui)
    implementation(libs.configurate)
    implementation(libs.simplixstorage)
    implementation(libs.cronutils)

    // Plugin libs - these will be present as other plugins
    compileOnly(libs.helper)

    // The Minecraft server API
    compileOnly(libs.server.paper)

    // Other random plugins that we may interact with
    compileOnly(libs.luckperms)
    compileOnly(libs.vault) { isTransitive = false }
    compileOnly(libs.essentials) { isTransitive = false }
    compileOnly(libs.towny)
    compileOnly(libs.itemsadder)
    compileOnly(libs.mythiclib)
    compileOnly(libs.mmoitems)
    compileOnly(libs.interactivebooks)
    compileOnly(libs.brewery) { isTransitive = false }
    compileOnly(libs.papi) { isTransitive = false }
    compileOnly(libs.minipapi) { isTransitive = false }

    testImplementation(libs.server.paper)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.helper)
}

tasks {
    shadowJar {
        /*configurations = listOf(
            project.configurations.implementation.get(),
            project.configurations.compileOnlyApi.get()
        )*/

        // Paper Plugins have isolated classloaders so relocating classes is no longer needed
    }
}
