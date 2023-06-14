dependencies {
    // server api
    compileOnlyApi(libs.server.paper)

    // libs that need to be shaded
    compileOnlyApi(libs.evalex)

    // libs that present as other plugins
    compileOnlyApi(project(":mewcore"))
    compileOnlyApi(libs.helper)
    compileOnlyApi(libs.betonquest) {
        exclude("io.papermc")
        exclude("com.comphenix.packetwrapper")
        exclude("me.filoghost.holographicdisplays")
    }
}
