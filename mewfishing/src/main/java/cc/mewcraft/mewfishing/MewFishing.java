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

    public static MewFishing instance() {
        return p;
    }

    public void log(String message) {
        p.getLogger().info(message);
    }

    public void log(Level level, String message) {
        p.getLogger().log(level, message);
    }

    public void log(String module, boolean status) {
        p.getLogger().info(status ? module + " is enabled" : module + " is disabled");
    }

    @Override protected void enable() {
        p = this;

        // ---- Load config ----
        config = new MewConfig(this);
        config.loadDefaultConfig();

        // ---- Load messages ----
        translations = new Translations(this);

        // ---- Register modules ----
        autoFish = bindModule(new AutoFishModule(this));
        fishPower = bindModule(new FishPowerModule(this));
        fishLoot = bindModule(new FishLootModule(this));

        // ---- Register commands ----
        try {
            new CommandManager(this);
        } catch (Exception e) {
            getLogger().severe("Failed to initialise commands!");
            e.printStackTrace();
        }
    }

    public Translations lang() {
        return p.translations;
    }

    public MewConfig config() {
        return p.config;
    }

    public AutoFishModule autoFishModule() {
        return autoFish;
    }

    public FishPowerModule fishingPowerModule() {
        return fishPower;
    }

    public FishLootModule fishingLootModule() {
        return fishLoot;
    }

}
