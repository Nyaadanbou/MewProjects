plugins {
    id("cc.mewcraft.base")
}

dependencies {
    compileOnlyApi(project(":mewcore"))
    compileOnlyApi("org.purpurmc.purpur", "purpur-api", "1.19.4-R0.1-SNAPSHOT")
    compileOnlyApi("me.lucko", "helper", "5.6.13")
}