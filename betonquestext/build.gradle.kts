// Leave it so that I can know I can write the `plugins` block like this
/*plugins {
    id("io.freefair.lombok") version "8.0.1" apply false // https://docs.gradle.org/current/userguide/plugins.html#sec:subprojects_plugins_dsl
}*/

subprojects {
    apply(plugin = "cc.mewcraft.repo-conventions")
    apply(plugin = "cc.mewcraft.java-conventions")

    group = "cc.mewcraft.betonquest"
}
