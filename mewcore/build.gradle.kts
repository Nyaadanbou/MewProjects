plugins {
    id("cc.mewcraft.repo-conventions")
    id("cc.mewcraft.java-conventions")
    id("cc.mewcraft.deploy-conventions")
    id("cc.mewcraft.publishing-conventions")
    id("cc.mewcraft.paper-plugins")
}

project.ext.set("name", "MewCore")

group = "cc.mewcraft"
version = "5.17.4"
description = "Common code of all Mewcraft plugins."

// Reference: https://youtrack.jetbrains.com/issue/IDEA-276365
/*configurations {
    compileOnly {
        isTransitive = false
    }
}*/

dependencies {
    // Dependencies at [compile path]
    // These dependencies are essential for plugin development,
    // so we just expose them to the consumers at compile path.
    compileOnlyApi(libs.guice)
    compileOnlyApi(libs.lang.bukkit)
    compileOnlyApi(libs.bundles.cmds.paper) {
        exclude("net.kyori")
    }

    // Dependencies at [runtime path]
    // These dependencies will be shaded in the final jar.
    // Consumers are expected to declare these dependencies
    // at [compile path] if they need these dependencies.

    // Dependency injection
    runtimeOnly(libs.guice)
    // i18n
    runtimeOnly(libs.lang.bukkit)
    // Commands
    runtimeOnly(libs.bundles.cmds.paper)
    // Database
    runtimeOnly(libs.hikari)
    // GUIs
    runtimeOnly(libs.anvilgui)
    // Configuration
    runtimeOnly(libs.configurate)
    runtimeOnly(libs.simplixstorage)
    // Utilities
    runtimeOnly(libs.commons.io)
    runtimeOnly(libs.cronutils)
    runtimeOnly(libs.authlib) {
        exclude("com.google.guava")
        exclude("com.google.code.gson")
        exclude("com.google.code.findbugs")
        exclude("org.apache.logging.log4j")
    }

    // Libs embedded by core (itself)
    compileOnly(libs.authlib)
    compileOnly(libs.configurate)

    // Plugin libs - these will be present as other plugins
    compileOnly(libs.helper)

    // The Minecraft server API
    compileOnly(libs.server.paper)

    // Other random plugins that we may interact with
    compileOnly(libs.luckperms)
    compileOnly(libs.vault) { isTransitive = false }
    compileOnly(libs.essentials) { isTransitive = false }
    compileOnly(libs.towny) { isTransitive = false }
    compileOnly(libs.itemsadder) { isTransitive = false }
    compileOnly(libs.mythiclib) { isTransitive = false }
    compileOnly(libs.mmoitems) { isTransitive = false }
    compileOnly(libs.interactivebooks) { isTransitive = false }
    compileOnly(libs.brewery) { isTransitive = false }
    compileOnly(libs.papi) { isTransitive = false }
    compileOnly(libs.minipapi) { isTransitive = false }

    testImplementation(libs.junit)
    testImplementation(libs.helper)
    testImplementation(libs.server.paper)
}
