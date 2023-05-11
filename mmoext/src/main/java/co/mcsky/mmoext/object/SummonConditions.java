package co.mcsky.mmoext.object;

import cc.mewcraft.mewcore.util.UtilTowny;
import co.mcsky.mmoext.HookId;
import co.mcsky.mmoext.Main;
import co.mcsky.mmoext.SimpleHook;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.lucko.helper.cooldown.Cooldown;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.BoundingBox;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class SummonConditions {

    // World conditions
    private final Set<String> worlds;

    // Biome conditions
    private final EnumSet<Biome> biomes;

    // Height conditions
    private double minHeight;
    private double maxHeight;

    // Open space conditions
    private double openRadius;
    private double openHeight;

    // Wilderness condition
    private boolean wilderness;

    // Check nearby active mobs before summon
    private double checkActiveRadius;

    // Per player per item cooldown, in seconds
    private int cooldown;
    private final Map<UUID, Cooldown> cooldownMap;

    public SummonConditions() {
        this.worlds = new HashSet<>();
        this.biomes = EnumSet.noneOf(Biome.class);
        this.minHeight = -64;
        this.maxHeight = 320;
        this.openRadius = 0;
        this.openHeight = 0;
        this.wilderness = false;
        this.checkActiveRadius = 0;
        this.cooldown = 0;
        this.cooldownMap = new HashMap<>();
    }

    public void addWorld(String world) {
        worlds.add(world);
    }

    public void addBiome(Biome biome) {
        biomes.add(biome);
    }

    public void setMinHeight(double minHeight) {
        this.minHeight = minHeight;
    }

    public void setMaxHeight(double maxHeight) {
        this.maxHeight = maxHeight;
    }

    public void setOpenRadius(double openRadius) {
        this.openRadius = openRadius;
    }

    public void setOpenHeight(double openHeight) {
        this.openHeight = openHeight;
    }

    public void setWilderness(boolean wilderness) {
        this.wilderness = wilderness;
    }

    public void setCheckActiveRadius(double checkActiveRadius) {
        this.checkActiveRadius = checkActiveRadius;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public boolean testWorld(String world) {
        return worlds.contains(world);
    }

    public boolean testBiome(Biome biome) {
        return biomes.contains(biome);
    }

    public boolean testHeight(double height) {
        return height >= minHeight && height <= maxHeight;
    }

    public boolean testOpenSpace(Location location) {
        BoundingBox box = BoundingBox.of(location.getBlock());
        box.expand(BlockFace.EAST, openRadius);
        box.expand(BlockFace.SOUTH, openRadius);
        box.expand(BlockFace.WEST, openRadius);
        box.expand(BlockFace.NORTH, openRadius);
        box.expand(BlockFace.UP, openHeight);
        box.shift(0, 1, 0);
        for (int y = (int) box.getMinY(); y < (int) box.getMaxY(); y++) {
            for (int x = (int) box.getMinX(); x < (int) box.getMaxX(); x++) {
                for (int z = (int) box.getMinZ(); z < (int) box.getMaxZ(); z++) {
                    if (location.getWorld().getBlockAt(x, y, z).isSolid()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean testWilderness(Location location) {
        return !SimpleHook.hasPlugin(HookId.TOWNY) || !wilderness || UtilTowny.isInWilderness(location);
    }

    public long cooldownRemaining(UUID uuid, String itemId) {
        var summon = Main.config().getSummonItem(itemId);
        if (summon.isEmpty()) return 0L;
        return cooldownMap
            .computeIfAbsent(uuid, id -> Cooldown.of(this.cooldown, TimeUnit.SECONDS))
            .remainingTime(TimeUnit.SECONDS);
    }

    public boolean testCooldown(UUID uuid, String itemId) {
        var summon = Main.config().getSummonItem(itemId);
        if (summon.isEmpty()) return true;
        return cooldownMap
            .computeIfAbsent(uuid, id -> Cooldown.of(this.cooldown, TimeUnit.SECONDS))
            .test();
    }

    public boolean testCooldownSilently(UUID uuid, String itemId) {
        var summon = Main.config().getSummonItem(itemId);
        if (summon.isEmpty()) return true;
        return cooldownMap
            .computeIfAbsent(uuid, id -> Cooldown.of(this.cooldown, TimeUnit.SECONDS))
            .testSilently();
    }

    public boolean testNearbyActiveMobs(Location location, String mobId) {
        var nearby = location.getNearbyLivingEntities(checkActiveRadius);
        for (LivingEntity entity : nearby) {
            var mobInstance = MythicBukkit.inst().getMobManager().getMythicMobInstance(entity);
            if (mobInstance != null && mobInstance.getMobType().equalsIgnoreCase(mobId)) {
                return false;
            }
        }
        return true;
    }

}
