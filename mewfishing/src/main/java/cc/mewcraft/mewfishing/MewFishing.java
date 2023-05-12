package cc.mewcraft.mewfishing;

import cc.mewcraft.mewcore.message.Translations;
import cc.mewcraft.mewfishing.command.CommandManager;
import cc.mewcraft.mewfishing.module.AutoFishModule;
import cc.mewcraft.mewfishing.module.FishLootModule;
import cc.mewcraft.mewfishing.module.FishPowerModule;
import me.lucko.helper.plugin.ExtendedJavaPlugin;

import java.util.logging.Level;

public final class MewFishing extends ExtendedJavaPlugin {

    private static MewFishing p;

    // internal
    public MewConfig config;
    public Translations translations;

    // modules
    private FishPowerModule fishPower;
    private AutoFishModule autoFish;
    private FishLootModule fishLoot;

    public static void debug(String message) {
        if (conf().debug()) log(Level.INFO, "[DEBUG] " + message);
    }

    public static MewFishing instance() {
        return p;
    }

    public static Translations translations() {
        return p.translations;
    }

    public static MewConfig conf() {
        return p.config;
    }

    public static void log(String message) {
        p.getLogger().info(message);
    }

    public static void log(Level level, String message) {
        p.getLogger().log(level, message);
    }

    public static void log(String module, boolean status) {
        if (status) {
            p.getLogger().info(module + " is enabled");
        } else {
            p.getLogger().info(module + " is disabled");
        }
    }

    public FishPowerModule getFishPowerModule() {
        return fishPower;
    }

    public AutoFishModule getAutoFishModule() {
        return autoFish;
    }

    public FishLootModule getFishLootModule() {
        return fishLoot;
    }

    @Override
    protected void enable() {
        p = this;

        // ---- Load config ----
        config = new MewConfig(this);
        config.loadDefaultConfig();

        // ---- Load messages ----
        translations = new Translations(this);

        // ---- Register modules ----
        autoFish = bindModule(new AutoFishModule());
        fishPower = bindModule(new FishPowerModule());
        fishLoot = bindModule(new FishLootModule());

        // ---- Register commands ----
        try {
            new CommandManager(this);
        } catch (Exception e) {
            getLogger().severe("Failed to initialise commands!");
            e.printStackTrace();
        }
    }

    @Override
    protected void disable() {}

}
