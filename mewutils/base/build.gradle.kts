plugins {
    id("cc.mewcraft.repo-conventions")
    id("cc.mewcraft.java-conventions")
}

dependencies {
    // core libs
    compileOnlyApi(project(":mewcore"))
    // libs from core
    compileOnly(libs.configurate)

    compileOnlyApi(libs.server.paper)
    compileOnlyApi(libs.helper)
}