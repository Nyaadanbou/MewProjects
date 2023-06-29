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
    compileOnly {
        isTransitive = false
    }
}*/

dependencies {
    // Shaded libs at compile path - these are essential for plugin development
    compileOnlyApi(libs.guice)
    compileOnlyApi(libs.lang.bukkit)
    compileOnlyApi(libs.bundles.cmds) {
        exclude("net.kyori")
    }

    // Shaded libs at runtime path - these are optional
    // Consumers are expected to declare these dependencies
    // at [compile path] if they need these dependencies
    runtimeOnly(libs.hikari)
    runtimeOnly(libs.anvilgui)
    runtimeOnly(libs.bundles.invui)
    runtimeOnly(libs.configurate)
    runtimeOnly(libs.simplixstorage)
    runtimeOnly(libs.cronutils)
    runtimeOnly(libs.authlib) {
        exclude("com.google.guava")
        exclude("com.google.code.gson")
        exclude("com.google.code.findbugs")
        exclude("org.apache.logging.log4j")
    }

    // Libs provided by core (itself)
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

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.server.paper)
    testImplementation(libs.helper)
}
