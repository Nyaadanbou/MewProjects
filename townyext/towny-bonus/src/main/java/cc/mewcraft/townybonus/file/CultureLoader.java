package cc.mewcraft.townybonus.file;

import cc.mewcraft.townybonus.object.bonus.Bonus;
import cc.mewcraft.townybonus.object.culture.Culture;
import cc.mewcraft.townybonus.object.culture.CultureLevel;
import cc.mewcraft.townybonus.TownyBonus;
import com.google.common.base.Preconditions;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * A loader to read all culture files.
 */
public final class CultureLoader {

    // culture folder
    private final File cultureFolder;

    // all loaded bonus
    private final Map<String, Bonus> bonusMap;

    /**
     * @param cultureFolder a folder containing all culture files
     */
    public CultureLoader(File cultureFolder, File bonusFolder) throws Exception {
        this.cultureFolder = cultureFolder;

        // Use BonusLoader to load all bonus
        this.bonusMap = new BonusLoader(bonusFolder).loadAll();
    }

    public Set<Culture> loadAll() throws Exception {
        Set<Culture> cultureSet = new HashSet<>();

        final Collection<File> cultureFiles = FileUtils.listFiles(cultureFolder, new String[]{"yml"}, true);
        for (File cultureFile : cultureFiles) {
            final Culture culture = load(cultureFile);
            cultureSet.add(culture);
            TownyBonus.debug("Finished loading culture: " + culture.getName());
        }

        return cultureSet;
    }

    private Culture load(File file) throws Exception {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getPath() + " does not exist");
        }

        TownyBonus.debug("Found culture file: " + file.getPath());
        FileConfiguration config = new YamlConfiguration();
        config.load(file);

        // start constructing bonus object from config

        final String name = Preconditions.checkNotNull(config.getString("name"), "'name' cannot be null");
        final String pageUrl = Preconditions.checkNotNull(config.getString("pageUrl"), "'pageUrl' cannot be null");
        final ConfigurationSection configSec = Preconditions.checkNotNull(config.getConfigurationSection("levels"), "'levels' cannot be null");
        final List<String> keys = configSec.getKeys(false).stream().toList();

        TownyBonus.debug("  Got culture name: " + name);
        TownyBonus.debug("  Got culture page URL: " + pageUrl);
        Culture culture = new Culture(name, pageUrl);

        for (String key : keys) {
            final int cultureLevel = Integer.parseInt(key);
            final CultureLevel cultureLevelObj = new CultureLevel(cultureLevel);
            TownyBonus.debug("      Found culture level: " + cultureLevel);

            final List<String> bonusNameList = config.getStringList("levels." + key);
            for (String bonusName : bonusNameList) {
                final Bonus bonus = bonusMap.get(bonusName);

                if (bonus == null) {
                    throw new IOException("         Bonus not found: " + bonusName);
                }

                cultureLevelObj.addBonus(bonus);
                TownyBonus.debug("          Adding bonus \"" + bonusName + "\" to culture level " + cultureLevel);
            }

            TownyBonus.debug("      Finished loading current culture level %s, adding to culture %s".formatted(cultureLevel, name));
            culture.addCultureLevel(cultureLevelObj);
        }

        return culture;
    }

}
