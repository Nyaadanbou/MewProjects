package cc.mewcraft.townybonus;

import cc.mewcraft.townybonus.command.CommandManager;
import cc.mewcraft.townybonus.file.CultureLoader;
import cc.mewcraft.townybonus.listener.*;
import cc.mewcraft.townybonus.object.culture.Culture;
import cc.mewcraft.townybonus.util.UtilFile;
import com.google.common.base.Preconditions;
import me.lucko.helper.Services;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import net.milkbowl.vault.economy.Economy;

import java.io.File;
import java.net.URL;
import java.util.Set;
import java.util.logging.Level;

public final class TownyBonus extends ExtendedJavaPlugin {

    public static TownyBonus p;

    public TownyBonusConfig config;
    public TownyBonusMessages messages;
    public CommandManager commands;

    public Cultures cultureManager;

    public Economy eco;

    public static void debug(String message) {
        if (TownyBonus.config().getDebug())
            TownyBonus.p.getLogger().info("[DEBUG] " + message);
    }

    public static void debug(Throwable message) {
        if (TownyBonus.config().getDebug())
            TownyBonus.p.getLogger().info("[DEBUG] " + message.getMessage());
    }

    public static TownyBonusConfig config() {
        return TownyBonus.p.config;
    }

    public static TownyBonusMessages lang() {
        return TownyBonus.p.messages;
    }

    @Override
    protected void enable() {
        // re-assign instance
        p = this;

        config = new TownyBonusConfig(this);
        config.loadDefaultConfig();

        messages = new TownyBonusMessages(this);

        try {
            commands = new CommandManager(this);
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "Failed to initialize commands", e);
        }

        // hook into external
        try {
            eco = Services.load(Economy.class);
        } catch (Exception e) {
            this.getLogger().severe(e.getMessage());
            this.disable();
            return;
        }

        this.loadData();

        this.bindModule(new EntityDeathListener());
        this.bindModule(new UpkeepNationListener());
        this.bindModule(new UpkeepTownListener());
        this.bindModule(new BonusListener());
        this.bindModule(new StatusScreenListener());
    }

    @Override
    protected void disable() {

    }

    public void reloadLang() {
        messages = new TownyBonusMessages(this);
    }

    public void loadData() {
        try {
            final File cultureFolder = new File(this.getDataFolder(), "cultures");
            final File bonusFolder = new File(this.getDataFolder(), "bonus");

            if (cultureFolder.mkdirs()) {
                TownyBonus.debug("Cultures folder does not exist, copying default.");
                final URL culturesUrl = TownyBonus.p.getClassLoader().getResource("cultures");
                Preconditions.checkNotNull(culturesUrl, "culturesUrl is null");
                UtilFile.copyResourcesRecursively(culturesUrl, cultureFolder);
            }
            if (bonusFolder.mkdirs()) {
                TownyBonus.debug("Bonus folder does not exist, copying default.");
                final URL bonusUrl = TownyBonus.p.getClassLoader().getResource("bonus");
                Preconditions.checkNotNull(bonusUrl, "bonusUrl is null");
                UtilFile.copyResourcesRecursively(bonusUrl, bonusFolder);
            }

            // Use loader to load all data
            final CultureLoader cultureLoader = new CultureLoader(cultureFolder, bonusFolder);
            final Set<Culture> loadedCultures = cultureLoader.loadAll();

            // Put the data into culture manager
            cultureManager = new Cultures(this);
            loadedCultures.forEach(c -> cultureManager.put(c));
        } catch (Exception e) {
            this.getLogger().severe(e.getMessage());
            this.disable();
        }
    }
}
