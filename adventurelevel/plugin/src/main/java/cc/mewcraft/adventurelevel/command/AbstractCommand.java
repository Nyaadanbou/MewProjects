package cc.mewcraft.adventurelevel.command;

import cc.mewcraft.adventurelevel.AdventureLevelPlugin;

public abstract class AbstractCommand {

    protected final AdventureLevelPlugin plugin;
    protected final CommandManager manager;

    public AbstractCommand(AdventureLevelPlugin plugin, CommandManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    public abstract void register();

}

