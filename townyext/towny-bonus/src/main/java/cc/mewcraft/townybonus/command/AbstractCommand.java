package cc.mewcraft.townybonus.command;


import cc.mewcraft.townybonus.TownyBonus;

public abstract class AbstractCommand {

    protected final TownyBonus plugin;
    protected final CommandManager manager;

    public AbstractCommand(TownyBonus plugin, CommandManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    public abstract void register();

}

