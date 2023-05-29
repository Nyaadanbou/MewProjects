plugins {
    id("cc.mewcraft.base")
}

dependencies {
    compileOnlyApi(project(":mewcore"))
    compileOnlyApi(libs.server.paper)
    compileOnlyApi(libs.helper)
}