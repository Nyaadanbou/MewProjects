package cc.mewcraft.nms;

import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public interface MewNMS {
    void useItem(@NotNull Player player, @NotNull EquipmentSlot hand);

    @NotNull Key biomeKey(@NotNull Location location);
}
