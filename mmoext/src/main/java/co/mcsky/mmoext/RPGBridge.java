package co.mcsky.mmoext;

import co.mcsky.mmoext.item.ItemsAdderUIFilter;
import co.mcsky.mmoext.listener.ItemsAdderListener;
import co.mcsky.mmoext.listener.MythicMobListener;
import co.mcsky.mmoext.listener.PlayerAttackListener;
import co.mcsky.mmoext.listener.SummonListener;
import me.lucko.helper.plugin.ExtendedJavaPlugin;

import java.util.logging.Level;

public class RPGBridge extends ExtendedJavaPlugin {

    private static RPGBridge p;

    // whether ItemsAdder has loaded all data
    public static boolean ITEMSADDER_LOADED = false;

    private MMOConfig config;
    private MMOCommands commands;
    private MMOLanguages languages;

    public static RPGBridge inst() {
        return p;
    }

    public static MMOConfig config() {
        return p.config;
    }

    public static MMOLanguages lang() {
        return p.languages;
    }

    public static void log(Level level, String info) {
        p.getLogger().log(level, info);
    }

    public static void reload() {
        // Reload config and languages
        p.config.loadDefaultConfig();
        p.config.loadSummonItems();
        p.languages = new MMOLanguages(p);
    }

    @Override
    protected void enable() {
        p = this;

        commands = new MMOCommands(p);
        commands.register();

        config = new MMOConfig(p);
        config.loadDefaultConfig();

        languages = new MMOLanguages(p);

        if (SimpleHook.hasPlugin(HookId.MYTHIC_LIB) && SimpleHook.hasPlugin(HookId.ITEMS_ADDER)) {
            bind(new ItemsAdderListener());
            ItemsAdderUIFilter.register();
        }

        if (SimpleHook.hasPlugin(HookId.MYTHIC_MOBS) && SimpleHook.hasPlugin(HookId.ITEMS_ADDER)) {
            bind(new SummonListener());
        }

        if (SimpleHook.hasPlugin(HookId.MYTHIC_MOBS) && SimpleHook.hasPlugin(HookId.MYTHIC_LIB)) {
            bind(new MythicMobListener());
            bind(new PlayerAttackListener());
        }
    }

    @Override
    protected void disable() {}

}
