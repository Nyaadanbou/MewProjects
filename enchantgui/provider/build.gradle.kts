dependencies {
    // dependent module
    compileOnly(project(":enchantgui:api"))

    // the server api
    compileOnly(libs.server.paper)

    // core libs
    compileOnly(project(":mewcore"))

    // libs present as other plugins
    compileOnly(libs.helper)
    compileOnly("su.nexmedia", "NexEngine", "2.2.10")
    compileOnly("su.nexmedia", "NexEngineAPI", "2.2.10")
    compileOnly("su.nightexpress.excellentenchants", "Core", "3.4.3") {
        isTransitive = false
    }
}