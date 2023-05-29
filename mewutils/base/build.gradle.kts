plugins {
    id("cc.mewcraft.java-conventions")
    id("cc.mewcraft.repository-conventions")
}

dependencies {
    compileOnlyApi(project(":mewcore"))
    compileOnlyApi(libs.server.paper)
    compileOnlyApi(libs.helper)
}