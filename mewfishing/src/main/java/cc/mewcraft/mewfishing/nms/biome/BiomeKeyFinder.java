package cc.mewcraft.mewfishing.nms.biome;

import net.kyori.adventure.key.Key;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

public class BiomeKeyFinder {
    public static @NotNull String getBiomeKeyString(Location location) {
        Registry<Biome> biomes = RegistryFetcher.biomeRegistry();
        ServerLevel serverlevel = ((CraftWorld) location.getWorld()).getHandle();

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        Holder<Biome> biome = serverlevel.getNoiseBiome(x >> 2, y >> 2, z >> 2);
        ResourceLocation key = biomes.getKey(biome.value());

        if (key == null)
            throw new NullPointerException("Biome not found at: (%s,%s,%s)".formatted(x, y, z));

        return key.toString();
    }

    public static @NotNull Key getBiomeKey(Location location) {
        @Subst("terra:overworld/overworld/random_biome")
        String keyString = getBiomeKeyString(location);
        return Key.key(keyString);
    }
}
