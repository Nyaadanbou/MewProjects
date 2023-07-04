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

public class ReforgeWindowWrapper {
    private final ReforgePlugin plugin;
    final ReforgeGuiWrapper guiWrapper;
    final Window.Builder.Normal.Single window;

    @Inject
    public ReforgeWindowWrapper(final ReforgePlugin plugin, final ReforgeGuiWrapper guiWrapper) {
        this.plugin = plugin;
        this.guiWrapper = guiWrapper;

        this.window = Window.single()
            .setTitle(translatable("menu.reforge.title"))
            .setGui(guiWrapper.gui);
    }

    public void open(Player viewer) {
        window.addOpenHandler(() -> {
            // Play sound when opening
            @Subst("minecraft:entity.villager.work_weaponsmith")
            String string = Objects.requireNonNull(plugin.getConfig().getString("reforge_sound.start"));
            viewer.playSound(Sound.sound(Key.key(string), Sound.Source.MASTER, 1f, 1f));
        });

        window.addCloseHandler(() -> {
            // Return all items to player inventory if closing window
            PlayerInventory playerInventory = viewer.getInventory();
            addItem(playerInventory, guiWrapper.transformInventory.getItems());
            addItem(playerInventory, guiWrapper.ingredientInventory.getItems());
            addItem(playerInventory, guiWrapper.outputInventory.getItems());
        });

        window.open(viewer);
    }

    private void addItem(@NotNull PlayerInventory playerInventory, @Nullable ItemStack @NotNull ... items) {
        for (final ItemStack item : items) {
            if (item != null) {
                playerInventory.addItem(item);
            }
        }
    }
}
