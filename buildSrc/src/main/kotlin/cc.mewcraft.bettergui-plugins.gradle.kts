plugins {
    id("cc.mewcraft.java-conventions")
    id("cc.mewcraft.deploy-conventions")
    id("cc.mewcraft.repository-conventions")
}

dependencies {
    compileOnly("io.papermc.paper", "paper-api", "1.19.4-R0.1-SNAPSHOT")
    compileOnly("me.hsgamer", "bettergui", "8.8", classifier = "shaded")
    /*compileOnly("me.hsgamer", "hscore-minecraft-gui-advanced", "4.2.7") {
        exclude("me.hsgamer", "hscore-ui")
        exclude("me.hsgamer", "hscore-minecraft-gui")
    }*/
}

tasks {
    shadowJar {
        minimize()
        relocate("me.hsgamer.hscore", "me.hsgamer.bettergui.lib.core")
        relocate("org.bstats", "me.hsgamer.bettergui.lib.bstats")
    }
    processResources {
        filesMatching("**/addon.yml") {
            val mappings = mapOf(
                "version" to "${project.version}",
                "description" to project.description
            )
            expand(mappings)
        }
    }
}
