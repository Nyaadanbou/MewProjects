package cc.mewcraft.townyboard;

import cc.mewcraft.mewcore.message.Translations;
import cc.mewcraft.townyboard.command.PluginCommands;
import cc.mewcraft.townyboard.command_addon.NationSetLawCommand;
import cc.mewcraft.townyboard.command_addon.NationViewLawCommand;
import cc.mewcraft.townyboard.command_addon.TownSetLawCommand;
import cc.mewcraft.townyboard.command_addon.TownViewLawCommand;
import com.google.inject.AbstractModule;
import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;
import com.palmergames.bukkit.towny.TownyCommandAddonAPI;
import com.palmergames.bukkit.towny.TownyCommandAddonAPI.CommandType;
import com.palmergames.bukkit.towny.object.AddonCommand;
import me.lucko.helper.plugin.ExtendedJavaPlugin;

public class TownyBoardPlugin extends ExtendedJavaPlugin {
    private Translations translations;

    public Translations getLang() {
        return translations;
    }

    @Override protected void enable() {
        saveDefaultConfig();
        reloadConfig();

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
                bind(TownyBoardPlugin.class).toInstance(TownyBoardPlugin.this);
            }
        });

        translations = new Translations(this, "languages");

        // Register commands
        try {
            injector.getInstance(PluginCommands.class).registerCommands();
        } catch (ConfigurationException | ProvisionException e) {
            getSLF4JLogger().error("Failed to register commands!", e);
        }

        // Register Towny command addons
        TownyCommandAddonAPI.addSubCommand(new AddonCommand(CommandType.TOWN, "law", injector.getInstance(TownViewLawCommand.class)));
        TownyCommandAddonAPI.addSubCommand(new AddonCommand(CommandType.TOWN_SET, "law", injector.getInstance(TownSetLawCommand.class)));
        TownyCommandAddonAPI.addSubCommand(new AddonCommand(CommandType.NATION, "law", injector.getInstance(NationViewLawCommand.class)));
        TownyCommandAddonAPI.addSubCommand(new AddonCommand(CommandType.NATION_SET, "law", injector.getInstance(NationSetLawCommand.class)));
    }

    @Override protected void disable() {
        // Remove command addons to allow reload
        TownyCommandAddonAPI.removeSubCommand(CommandType.TOWN, "law");
        TownyCommandAddonAPI.removeSubCommand(CommandType.TOWN_SET, "law");
        TownyCommandAddonAPI.removeSubCommand(CommandType.NATION, "law");
        TownyCommandAddonAPI.removeSubCommand(CommandType.NATION_SET, "law");
    }
}
