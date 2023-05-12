package cc.mewcraft.mewutils.api;

import org.spongepowered.configurate.ConfigurationNode;
import cc.mewcraft.mewcore.message.Translations;
import cc.mewcraft.mewutils.api.command.CommandRegistry;
import cc.mewcraft.mewutils.api.module.ModuleBase;
import org.bukkit.plugin.Plugin;

public interface MewPlugin extends Plugin {

    Translations getLang();

    ConfigurationNode getConfigNode();

    CommandRegistry getCommandRegistry();

    ClassLoader getClassLoader0();

    boolean isDevMode();

    boolean isModuleOn(ModuleBase module);

}
