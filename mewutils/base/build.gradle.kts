plugins {
    id("cc.mewcraft.java-conventions")
    id("cc.mewcraft.repository-conventions")
}

dependencies {
    // core libs
    compileOnlyApi(project(":mewcore"))
    // libs from core
    compileOnly(libs.configurate)

    compileOnlyApi(libs.server.paper)
    compileOnlyApi(libs.helper)
}