package cc.mewcraft.townybonus.file;

import cc.mewcraft.townybonus.object.bonus.*;
import cc.mewcraft.townybonus.util.UtilNumber;
import cc.mewcraft.townybonus.TownyBonus;
import cc.mewcraft.townybonus.util.Raffle;
import com.google.common.base.Preconditions;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * A loader to read all bonus files.
 */
public final class BonusLoader {

    private final File folder;

    /**
     * @param folder a folder containing all bonus file
     */
    public BonusLoader(File folder) {
        this.folder = folder;
    }

    public Map<String, Bonus> loadAll() throws Exception {
        Map<String, Bonus> bonusMap = new HashMap<>();
        final Collection<File> bonusFiles = FileUtils.listFiles(folder, new String[]{"yml"}, true);
        for (File bonusFile : bonusFiles) {
            final Bonus bonus = load(bonusFile);
            bonusMap.put(bonus.getName(), bonus);
        }
        return bonusMap;
    }

    private Bonus load(@NotNull File file) throws Exception {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getPath() + " does not exist");
        }

        TownyBonus.debug("Found bonus file: " + file.getPath());
        FileConfiguration config = new YamlConfiguration();
        config.load(file);

        // start constructing bonus object from config

        final String name = Preconditions.checkNotNull(config.getString("name"), "'name' cannot be null");
        final BonusType type = BonusType.valueOf(Preconditions.checkNotNull(config.getString("type"), "'type' cannot be null").trim().toUpperCase());
        final List<NoticeType> msgOpts = config.getStringList("messageOptions").stream().map(opt -> NoticeType.valueOf(opt.toUpperCase())).toList();
        TownyBonus.debug("  Got bonus name: " + name);
        TownyBonus.debug("  Got bonus type: " + type.name().toLowerCase());
        TownyBonus.debug("  Got message opts: " + msgOpts.stream().map(Enum::name).reduce((o1, o2) -> o1 + "," + o2).orElse("NONE"));

        switch (type) {
            case EXP -> {
                final int range = config.getInt("chance.range");
                final int number = config.getInt("chance.number");
                final double multiplier = config.getDouble("multiplier");
                final BonusExp inst = new BonusExp(name, Raffle.of(range, number), multiplier);
                msgOpts.forEach(opt -> inst.getNoticeOptions().add(opt));
                TownyBonus.debug("Finished loading bonus: " + name);
                return inst;
            }

            case MONEY -> {
                final int range1 = config.getInt("chance.range");
                final int number1 = config.getInt("chance.number");
                final int amountMin = UtilNumber.parseIntRangeLower(Objects.requireNonNull(config.getString("amount")));
                final int amountMax = UtilNumber.parseIntRangeHigher(Objects.requireNonNull(config.getString("amount")));
                final int range2 = config.getInt("jackpot.chance.range");
                final int number2 = config.getInt("jackpot.chance.number");
                final double multiplier = config.getDouble("jackpot.multiplier");

                final BonusMoney inst = new BonusMoney(name, amountMin, amountMax, multiplier, Raffle.of(range1, number1), Raffle.of(range2, number2));
                msgOpts.forEach(opt -> inst.getNoticeOptions().add(opt));
                TownyBonus.debug("Finished loading bonus: " + name);
                return inst;
            }

            case POTION_EFFECT -> {
                final int range = config.getInt("chance.range");
                final int number = config.getInt("chance.number");
                final int amountMin = UtilNumber.parseIntRangeLower(Objects.requireNonNull(config.getString("amount")));
                final int amountMax = UtilNumber.parseIntRangeHigher(Objects.requireNonNull(config.getString("amount")));

                final BonusPotionEffect inst = new BonusPotionEffect(name, Raffle.of(range, number), amountMin, amountMax);
                msgOpts.forEach(opt -> inst.getNoticeOptions().add(opt));

                final List<Map<?, ?>> effects = config.getMapList("effects");
                for (Map<?, ?> effect : effects) {
                    final PotionEffectType effectType = PotionEffectType.getByName(effect.get("type").toString());
                    Preconditions.checkNotNull(effectType, "Potion effect type not found: " + effect.get("type").toString());

                    final int amplifier = Integer.parseInt(effect.get("amplifier").toString());
                    final int duration = Integer.parseInt(effect.get("duration").toString());
                    final int weight = Integer.parseInt(effect.get("weight").toString());
                    inst.addEffect(effectType, duration, amplifier, weight);
                }

                TownyBonus.debug("Finished loading bonus: " + name);
                return inst;
            }

            case NATION_UPKEEP_MODIFIER -> {
                final double multiplier = config.getDouble("multiplier");

                final BonusUpkeepNation inst = new BonusUpkeepNation(name, multiplier);
                msgOpts.forEach(opt -> inst.getNoticeOptions().add(opt));
                TownyBonus.debug("Finished loading bonus: " + name);
                return inst;
            }

            case TOWN_UPKEEP_MODIFIER -> {
                final double multiplier = config.getDouble("multiplier");

                final BonusUpkeepTown inst = new BonusUpkeepTown(name, multiplier);
                msgOpts.forEach(opt -> inst.getNoticeOptions().add(opt));
                TownyBonus.debug("Finished loading bonus: " + name);
                return inst;
            }
        }

        throw new IOException("Error on loading: " + file.getPath());
    }

}
