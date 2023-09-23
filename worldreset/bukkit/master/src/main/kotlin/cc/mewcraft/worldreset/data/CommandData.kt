package cc.mewcraft.worldreset.data

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin

class CommandData(
    private val plugin: Plugin,
    commands: List<String>,
) {
    private val commands = commands.map { SingleCommandData(plugin, it) }

    fun dispatchAll() {
        commands.forEach { it.dispatch() }
    }
}

private class SingleCommandData(
    private val plugin: Plugin,
    private val command: String,
) {
    init {
        // Validate command line
        if (!isRegistered(command)) {
            plugin.componentLogger.warn("Command `$command` is not found in the CommandMap")
        }
    }

    fun dispatch() {
        if (!isRegistered(command)) {
            plugin.componentLogger.warn("Skipped command: `$command`")
        } else {
            plugin.componentLogger.info("Dispatching command: `$command`")
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command)
        }
    }

    private fun isRegistered(command: String) =
        command.splitToSequence(" ").first() in plugin.server.commandMap.knownCommands
}
