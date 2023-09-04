package cc.mewcraft.enchantment.gui.command

import cc.mewcraft.enchantment.gui.EnchantGuiPlugin
import cc.mewcraft.enchantment.gui.gui.EnchantMenu
import cc.mewcraft.mewcore.command.SimpleCommands
import cloud.commandframework.bukkit.parsers.PlayerArgument
import com.google.inject.Inject
import com.google.inject.Singleton
import org.bukkit.entity.Player

@Singleton
class PluginCommands @Inject constructor(
    plugin: EnchantGuiPlugin,
) : SimpleCommands<EnchantGuiPlugin>(plugin) {
    override fun prepareAndRegister() {
        // Prepare commands
        registry.prepareCommand(
            registry
                .commandBuilder("enchantgui")
                .literal("open")
                .permission("enchantgui.command.open")
                .argument(PlayerArgument.optional("target"))
                .handler {

                    val viewer: Player = if (it.contains("target")) {
                        it.get("target")
                    } else if (it.sender is Player) {
                        it.sender as Player
                    } else return@handler

                    plugin.injector.getInstance(EnchantMenu::class.java).showMenu(viewer)

                }.build()
        )
        registry.prepareCommand(
            registry
                .commandBuilder("enchantgui")
                .literal("reload")
                .permission("enchantgui.command.reload")
                .handler {

                    plugin.onDisable()
                    plugin.onEnable()

                }.build()
        )

        // Register commands
        registry.registerCommands()
    }
}
