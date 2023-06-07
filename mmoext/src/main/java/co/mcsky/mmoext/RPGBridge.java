package co.mcsky.mmoext;

import cc.mewcraft.mewcore.message.Translations;
import co.mcsky.mmoext.listener.SummonListener;
import me.lucko.helper.plugin.ExtendedJavaPlugin;

import java.util.logging.Level;

public class RPGBridge extends ExtendedJavaPlugin {

    private static RPGBridge p;

    private MMOConfig config;
    private MMOCommands commands;
    private Translations languages;

    public static RPGBridge inst() {
        return p;
    }

    public static MMOConfig config() {
        return p.config;
    }

    public static Translations lang() {
        return p.languages;
    }

    public static void log(Level level, String info) {
        p.getLogger().log(level, info);
    }

    public static void reload() {
        // Reload config and languages
        p.config.loadDefaultConfig();
        p.config.loadSummonItems();
        p.languages = new Translations(RPGBridge.p, "languages");
    }

    @Override protected void enable() {
        p = this;

        commands = new MMOCommands(p);
        commands.register();

        config = new MMOConfig(p);
        config.loadDefaultConfig();

        languages = new Translations(this, "languages");

        if (SimpleHook.hasPlugin(HookId.MYTHIC_MOBS) && SimpleHook.hasPlugin(HookId.ITEMS_ADDER)) {
            bind(new SummonListener());
        }
    }

}
