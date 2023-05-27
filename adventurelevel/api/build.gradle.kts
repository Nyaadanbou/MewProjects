plugins {
    id("cc.mewcraft.base")
    id("net.kyori.indra") version "3.0.1"
}

group = "cc.mewcraft.adventurelevel"
version = "1.0.0"
description = "Add adventure level to players"

dependencies {
    compileOnly(project(":mewcore"))

    // server api
    compileOnly("org.purpurmc.purpur", "purpur-api", "1.19.4-R0.1-SNAPSHOT")

    // libs that present as other plugins
    compileOnly("me.lucko", "helper", "5.6.13") { isTransitive = false }
}

indra {
    javaVersions().target(17)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
