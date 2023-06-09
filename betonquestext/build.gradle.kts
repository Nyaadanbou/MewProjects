plugins {
    id("io.freefair.lombok") version "8.0.1" apply false // https://docs.gradle.org/current/userguide/plugins.html#sec:subprojects_plugins_dsl
}

subprojects {
    apply(plugin = "cc.mewcraft.java-conventions")
    apply(plugin = "cc.mewcraft.repository-conventions")

    group = "cc.mewcraft.betonquest"
}
