package cc.mewcraft.mewfishing.command;

import cc.mewcraft.mewfishing.MewFish;

public abstract class AbstractCommand {

    protected final MewFish plugin;
    protected final CommandManager manager;

    public AbstractCommand(MewFish plugin, CommandManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    public abstract void register();

}

