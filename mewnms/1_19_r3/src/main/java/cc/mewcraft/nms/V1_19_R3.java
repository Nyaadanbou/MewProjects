package cc.mewcraft.nms;

import com.google.common.base.Preconditions;
import net.kyori.adventure.key.Key;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.biome.Biome;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

public class V1_19_R3 implements MewNMS {
    @Override public void useItem(final @NotNull Player player, final @NotNull EquipmentSlot hand) {
        Preconditions.checkNotNull(player);
        Preconditions.checkNotNull(hand);
        Preconditions.checkArgument(hand.isHand());

        InteractionHand nmsHand = hand == EquipmentSlot.HAND ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        serverPlayer.gameMode.useItem(serverPlayer, serverPlayer.getLevel(), serverPlayer.getItemInHand(nmsHand), nmsHand);
    }

    @Override public @NotNull Key biomeKey(final @NotNull Location location) {
        Preconditions.checkNotNull(location);

        CraftServer craftserver = (CraftServer) Bukkit.getServer();
        DedicatedServer dedicatedserver = craftserver.getServer();
        Registry<Biome> biomeRegistry = dedicatedserver.registryAccess().registryOrThrow(Registries.BIOME);

        ServerLevel serverlevel = ((CraftWorld) location.getWorld()).getHandle();

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        Holder<Biome> biomeHolder = serverlevel.getNoiseBiome(x >> 2, y >> 2, z >> 2);
        ResourceLocation resourceLocation = biomeRegistry.getKey(biomeHolder.value());

        if (resourceLocation == null) {
            throw new NullPointerException("Biome not found at: (%s,%s,%s)".formatted(x, y, z));
        }

        @Subst("terra:overworld/overworld/random_biome")
        String string = resourceLocation.toString();

        return Key.key(string);
    }
}
