plugins {
    id("cc.mewcraft.base")
    alias(libs.plugins.indra)
}

group = project(":adventurelevel:api").group
version = project(":adventurelevel:api").version
description = project(":adventurelevel:api").description

dependencies {
    // api module
    implementation(project(":adventurelevel:api"))

    // my own libs
    compileOnly(project(":mewcore"))

    // server api
    compileOnly("org.purpurmc.purpur", "purpur-api", "1.19.4-R0.1-SNAPSHOT")

    // libs that present as other plugins
    compileOnly("me.lucko", "helper", "5.6.13") { isTransitive = false }
    compileOnly("io.lumine", "MythicLib-dist", "1.6-SNAPSHOT")
    compileOnly("net.Indyuce", "MMOItems-API", "6.9.4-SNAPSHOT")
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
