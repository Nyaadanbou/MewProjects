package cc.mewcraft.worldreset

import cc.mewcraft.worldreset.data.BroadcastData
import cc.mewcraft.worldreset.data.CommandData
import cc.mewcraft.worldreset.data.CronData
import cc.mewcraft.worldreset.data.PathData
import cc.mewcraft.worldreset.schedule.*
import org.bukkit.configuration.ConfigurationSection

/**
 * Contains settings of this plugin.
 *
 * All the settings will be initialized as soon as this class is instantiated.
 *
 * If you need to reload the settings, simply construct a new instance again.
 */
class PluginSettings(
    private val plugin: WorldResetPlugin,
) {
    init {
        plugin.saveDefaultConfig()
        plugin.reloadConfig()
    }

    /* Public Methods */

    /**
     * List of schedules which are loaded from the config.
     */
    val schedules: List<Schedule> by lazy {
        buildList {
            with(plugin.config.getConfigurationSectionOrThrow("schedules")) schedules@{

                getKeys(false).toList().forEach { key ->
                    plugin.slF4JLogger.info("Loading schedule: $key")

                    with(getConfigurationSectionOrThrow(key)) schedule@{
                        val cron = CronData(plugin, this@schedule.getStringOrThrow("cron"))
                        val schedule = when (val type = this@schedule.getStringOrThrow("type")) {
                            "BROADCAST" -> loadBroadcastSchedule(key, cron, this@schedule)
                            "WORLD_RESET" -> loadWorldResetSchedule(key, cron, this@schedule)
                            "CONSOLE_COMMAND" -> loadConsoleCommandSchedule(key, cron, this@schedule)
                            "FILE_CLEANUP" -> loadFileCleanupSchedule(key, cron, this@schedule)
                            else -> throw IllegalArgumentException(type)
                        }
                        this@buildList += schedule
                    }
                }

            }
        }
    }

    /* Utility Methods */

    private fun loadBroadcastSchedule(
        name: String,
        cron: CronData,
        config: ConfigurationSection,
    ): BroadcastSchedule {
        val messages = config.getStringList("messages")
        return BroadcastSchedule(
            plugin, name, cron, BroadcastData(plugin, messages)
        )
    }

    private fun loadConsoleCommandSchedule(
        name: String,
        cron: CronData,
        config: ConfigurationSection,
    ): ConsoleCommandSchedule {
        val commands = config.getStringList("commands")
        return ConsoleCommandSchedule(
            plugin, name, cron, CommandData(plugin, commands)
        )
    }

    private fun loadFileCleanupSchedule(
        name: String,
        cron: CronData,
        config: ConfigurationSection,
    ): FileCleanupSchedule {
        val commands = config.getStringList("paths")
        return FileCleanupSchedule(
            plugin, name, cron, PathData(plugin, commands)
        )
    }

    private fun loadWorldResetSchedule(
        name: String,
        cron: CronData,
        config: ConfigurationSection,
    ): WorldResetSchedule {
        val world = config.getStringOrThrow("world")
        val keepSeed = config.getBoolean("keep_seed")
        val preCommands = config.getStringList("pre_commands")
        val postCommands = config.getStringList("post_commands")
        return WorldResetSchedule(
            plugin, name, cron, world, keepSeed, CommandData(plugin, preCommands), CommandData(plugin, postCommands),
        )
    }
}

/* Extension Functions */

private fun ConfigurationSection.getConfigurationSectionOrThrow(path: String) =
    this.getConfigurationSection(path) ?: throw NullPointerException(path)

private fun ConfigurationSection.getStringOrThrow(path: String) =
    this.getString(path) ?: throw NullPointerException(path)
