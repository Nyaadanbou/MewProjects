plugins {
    id("cc.mewcraft.java-conventions")
    id("cc.mewcraft.repository-conventions")
    id("cc.mewcraft.publishing-conventions")
    alias(libs.plugins.indra)
}

group = "cc.mewcraft.adventurelevel"
version = "1.1.0"
description = "Add adventure level to players"

dependencies {
    compileOnly(project(":mewcore"))

    // server api
    compileOnly(libs.server.paper)

    // libs that present as other plugins
    compileOnly(libs.helper) { isTransitive = false }
}

indra {
    javaVersions().target(17)
}

/*publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}*/
