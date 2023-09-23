import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("cc.mewcraft.deploy-conventions")
    alias(libs.plugins.pluginyml.paper)
}

project.ext.set("name", "WorldReset")

group = "cc.mewcraft.worldreset"
version = "1.0.0"
description = "Reset server worlds with cron expressions!"

dependencies {
    implementation(project(":worldreset:common"))

    // server api
    compileOnly(libs.server.paper)

    // plugin libs
    compileOnly(project(":mewcore"))
    compileOnly(libs.helper)
    compileOnly(libs.papi)
    compileOnly(libs.minipapi)

    // shaded libs
    implementation(project(":cronscheduler"))
    implementation(libs.cronutils)
    implementation(libs.bundles.mccoroutine.bukkit) {
        exclude("org.jetbrains.kotlin") // Excludes its Kotlin otherwise it breaks ours
    }
}

paper {
    main = "cc.mewcraft.worldreset.WorldResetPlugin"
    name = project.ext.get("name") as String
    version = "${project.version}"
    description = project.description
    apiVersion = "1.19"
    authors = listOf("Nailm")

    serverDependencies {
        register("Kotlin") {
            required = true
            joinClasspath = true
            load = PaperPluginDescription.RelativeLoadOrder.OMIT
        }
        register("helper") {
            required = true
            joinClasspath = true
            load = PaperPluginDescription.RelativeLoadOrder.OMIT
        }
        register("MewCore") {
            required = true
            joinClasspath = true
            load = PaperPluginDescription.RelativeLoadOrder.OMIT
        }
        register("MiniPlaceholders") {
            required = false
            joinClasspath = true
            load = PaperPluginDescription.RelativeLoadOrder.OMIT
        }
        register("PlaceholderAPI") {
            required = false
            joinClasspath = true
            load = PaperPluginDescription.RelativeLoadOrder.OMIT
        }
    }
}
