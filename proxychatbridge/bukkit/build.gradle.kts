plugins {
    id("cc.mewcraft.paper-plugins")
}

dependencies {
    compileOnly(project(":proxychatbridge:common"))
    compileOnly(libs.server.paper)
    compileOnly(libs.chatchat.api)
    compileOnly(libs.adventure.binaryserializer)
}
