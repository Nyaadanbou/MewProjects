subprojects {
    apply(plugin = "cc.mewcraft.repo-conventions")
    apply(plugin = "cc.mewcraft.java-conventions")

    group = "com.ranull"
    version = "1.6"
    description = "Sync server chat across a network"

    /*repositories {
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
            content {
                includeGroup("net.md-5")
                includeGroup("org.bstats")
            }
        }
    }*/
}