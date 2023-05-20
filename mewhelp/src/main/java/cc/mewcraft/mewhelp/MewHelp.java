package cc.mewcraft.mewhelp;

import me.lucko.helper.plugin.ExtendedJavaPlugin;

public final class MewHelp extends ExtendedJavaPlugin {

    public static MewHelp p;

    public MewConfig config;
    public MewMessages messages;
    public HelpCommand commands;

    public static void debug(String message) {
        if (p.config.getDebug())
            p.getLogger().info("[DEBUG] " + message);
    }

    public void reload() {
        disable();
        enable();
    }

    @Override
    protected void enable() {
        p = this;

        config = new MewConfig(this);
        config.loadDefaultConfig();

        messages = new MewMessages(this);
        commands = bindModule(new HelpCommand());
    }

    @Override
    protected void disable() {}
}
