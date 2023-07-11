package cc.mewcraft.reforge.gui.object;

import cc.mewcraft.reforge.gui.ReforgePlugin;
import com.google.inject.Inject;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.window.Window;

import java.util.Objects;

import static cc.mewcraft.reforge.gui.util.AdventureUtils.translatable;

public class ReforgeWindow {
    private final ReforgePlugin plugin;
    final ReforgeGui reforgeGui;
    final Window.Builder.Normal.Single reforgeWindow;

    @Inject
    public ReforgeWindow(final ReforgePlugin plugin, final ReforgeGui reforgeGui) {
        this.plugin = plugin;
        this.reforgeGui = reforgeGui;

        this.reforgeWindow = Window.single()
            .setTitle(translatable("menu.reforge.title"))
            .setGui(reforgeGui.gui);
    }

    public void open(Player viewer) {
        reforgeWindow.addOpenHandler(() -> {
            // Play sound when opening
            @Subst("minecraft:entity.villager.work_weaponsmith")
            String string = Objects.requireNonNull(plugin.getConfig().getString("reforge_sound.start"));
            viewer.playSound(Sound.sound(Key.key(string), Sound.Source.MASTER, 1f, 1f));
        });

        reforgeWindow.addCloseHandler(() -> {
            // Return all items to player inventory if closing window
            PlayerInventory playerInventory = viewer.getInventory();
            addItem(playerInventory, reforgeGui.transformInventory.getItems());
            addItem(playerInventory, reforgeGui.ingredientInventory.getItems());
            addItem(playerInventory, reforgeGui.outputInventory.getItems());
        });

        reforgeWindow.open(viewer);
    }

    private void addItem(@NotNull PlayerInventory playerInventory, @Nullable ItemStack @NotNull ... items) {
        for (final ItemStack item : items) {
            if (item != null) {
                playerInventory.addItem(item);
            }
        }
    }
}
