plugins {
    alias(libs.plugins.indra)
}

dependencies {
    // server api
    compileOnly(libs.server.paper)

    // libs that present as other plugins
    compileOnly(project(":mewcore"))
    compileOnly(libs.helper)
    compileOnly(libs.itemsadder)
    compileOnly(libs.mythiclib)
}

indra {
    javaVersions().target(17)
}
