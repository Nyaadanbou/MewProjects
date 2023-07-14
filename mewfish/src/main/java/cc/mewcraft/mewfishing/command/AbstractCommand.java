package cc.mewcraft.mewfishing.command;

import cc.mewcraft.mewfishing.MewFishing;

public abstract class AbstractCommand {

    protected final MewFishing plugin;
    protected final CommandManager manager;

    public AbstractCommand(MewFishing plugin, CommandManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    public abstract void register();

}

