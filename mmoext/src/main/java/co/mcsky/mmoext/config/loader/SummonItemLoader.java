package co.mcsky.mmoext.config.loader;

import co.mcsky.mmoext.Main;
import co.mcsky.mmoext.config.bean.ExplosionEffectConfig;
import co.mcsky.mmoext.config.bean.PotionEffectConfig;
import co.mcsky.mmoext.config.bean.SoundEffectConfig;
import co.mcsky.mmoext.object.SummonItem;
import com.google.common.base.Preconditions;
import dev.lone.itemsadder.api.CustomStack;
import io.lumine.mythic.api.MythicProvider;
import me.lucko.helper.utils.Log;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;

@SuppressWarnings("PatternValidation")
public class SummonItemLoader {

    public Set<SummonItem> readAll() {
        Set<SummonItem> items = new HashSet<>();
        var section = Main.inst().getConfig().getConfigurationSection("items");
        if (section == null) return items;

        section.getKeys(false).forEach(key -> {
            var sec = section.getConfigurationSection(key);
            read(sec).ifPresent(items::add);
        });

        return items;
    }

    public Optional<SummonItem> read(@Nullable ConfigurationSection section) {
        if (section == null) return Optional.empty();

        var mobId = section.getString("mobID");
        var itemId = section.getString("itemID");
        Preconditions.checkNotNull(mobId, "mobID");
        Preconditions.checkNotNull(itemId, "itemID");
        if (MythicProvider.get().getMobManager().getMythicMob(mobId).isEmpty()) {
            Log.severe("Loading failed! Unrecognized mob ID: " + mobId);
            return Optional.empty();
        }
        if (CustomStack.getInstance(itemId) == null) {
            Log.severe("Loading failed! Unrecognized item ID: " + itemId);
            return Optional.empty();
        }

        var summonItem = new SummonItem(itemId, mobId);

        // Read basics
        var mobLevel = section.getInt("mobLevel");
        var delaySpawn = section.getInt("delaySpawn");
        summonItem.setMobLevel(mobLevel);
        summonItem.setDelaySpawn(delaySpawn);

        // Read effects
        var effectsSec = section.getConfigurationSection("effects");
        if (effectsSec != null) {
            // Read sounds
            effectsSec.getMapList("sounds").forEach(map -> {
                var soundName = map.get("name").toString();
                var soundSource = map.get("source").toString();
                var soundPitch = Float.parseFloat(map.get("pitch").toString());
                var soundVolume = Float.parseFloat(map.get("volume").toString());
                var soundDelay = Integer.parseInt(map.get("delay").toString());
                var soundInst = Sound.sound(Key.key(soundName), Sound.Source.valueOf(soundSource.toUpperCase()), soundVolume, soundPitch);
                summonItem.getEffect().addSound(new SoundEffectConfig(soundInst, soundDelay));
            });
            // Read potions
            effectsSec.getMapList("potions").forEach(map -> {
                var type = PotionEffectType.getByName(map.get("type").toString());
                var amplifier = Integer.parseInt(map.get("amplifier").toString());
                var duration = Integer.parseInt(map.get("duration").toString());
                var ambient = Boolean.parseBoolean(map.get("ambient").toString());
                var particle = Boolean.parseBoolean(map.get("particles").toString());
                var icon = Boolean.parseBoolean(map.get("icon").toString());
                var radius = Double.parseDouble(map.get("radius").toString());
                summonItem.getEffect().addPotion(new PotionEffectConfig(new PotionEffect(type, duration, amplifier, ambient, particle, icon), radius));
            });
            // Read explosion
            if (effectsSec.getConfigurationSection("explosion") != null) {
                var explosionPower = (float) effectsSec.getDouble("explosion.power");
                var delay = effectsSec.getInt("explosion.delay");
                summonItem.getEffect().setExplosion(new ExplosionEffectConfig(explosionPower, delay));
            }
        }

        // Read conditions
        var cond = summonItem.getCondition();
        var condSec = section.getConfigurationSection("conditions");
        if (condSec != null) {
            var cooldown = condSec.getInt("cooldown");
            cond.setCooldown(cooldown);

            var checkActive = condSec.getDouble("checkActive");
            cond.setCheckActiveRadius(checkActive);

            condSec.getStringList("worlds").forEach(cond::addWorld);
            condSec.getStringList("biomes").forEach(biome -> cond.addBiome(Biome.valueOf(biome.toUpperCase())));
            if (condSec.getConfigurationSection("height") != null) {
                var min = condSec.getDouble("height.min");
                var max = condSec.getDouble("height.max");
                cond.setMinHeight(min);
                cond.setMaxHeight(max);
            }

            if (condSec.getConfigurationSection("space") != null) {
                var radius = condSec.getDouble("space.radius");
                var height = condSec.getDouble("space.height");
                cond.setOpenRadius(radius);
                cond.setOpenHeight(height);
            }

            cond.setWilderness(condSec.getBoolean("wilderness"));
        }

        Main.log(Level.INFO, "Summon Item Loaded: " + itemId + " -> " + mobId);

        return Optional.of(summonItem);
    }
}
