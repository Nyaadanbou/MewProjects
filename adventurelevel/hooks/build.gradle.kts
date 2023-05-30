plugins {
    id("cc.mewcraft.java-conventions")
    id("cc.mewcraft.repository-conventions")
    id("cc.mewcraft.publishing-conventions")
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
    compileOnly(libs.server.paper)

    // libs that present as other plugins
    compileOnly(libs.helper) { isTransitive = false }
    compileOnly(libs.mythiclib)
    compileOnly(libs.mmoitems)
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