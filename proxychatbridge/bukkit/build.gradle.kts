dependencies {
    compileOnly(project(":proxychatbridge:common"))
    compileOnly(libs.server.paper)
    compileOnly(libs.chatchat.api)
    compileOnly(libs.adventure.binaryserializer)
}

/*bukkit {
    main = "com.ranull.proxychatbridge.bukkit.ProxyChatBridge"
    name = "ProxyChatBridge"
    version = "${project.version}"
    apiVersion = "1.19"
    authors = listOf("Nailm", "Ranull")
    softDepend = listOf("ChatChat")
    permissions {
        register("proxychatbridge.command") {
            description = "Access to the ProxyChatBridge-Bukkit commands"
            default = BukkitPluginDescription.Permission.Default.OP
        }
    }
    commands {
        register("proxychatbridgebukkit") {
            description = "The entry to the ProxyChatBridge-Bukkit commands"
            usage = "/<command> reload"
        }
    }
}*/
