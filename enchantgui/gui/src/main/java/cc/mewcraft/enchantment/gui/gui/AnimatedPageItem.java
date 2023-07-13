package cc.mewcraft.enchantment.gui.gui;

import cc.mewcraft.enchantment.gui.config.EnchantGuiSettings;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.animation.impl.ColumnAnimation;
import xyz.xenondevs.invui.gui.SlotElement;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;

public abstract class AnimatedPageItem extends PageItem {
    private final boolean forward;
    private final EnchantGuiSettings settings;

    public AnimatedPageItem(final EnchantGuiSettings settings, final boolean forward) {
        super(forward);
        this.forward = forward;
        this.settings = settings;
    }

    @Override public void handleClick(final @NotNull ClickType clickType, final @NotNull Player player, final @NotNull InventoryClickEvent event) {
        if ((forward && getGui().hasNextPage()) || (!forward && getGui().hasPreviousPage())) {
            super.handleClick(clickType, player, event);
            player.playSound(settings.pageTurnSound());
            getGui().playAnimation(new ColumnAnimation(1, false), it ->
                it instanceof SlotElement.ItemSlotElement element && element.getItem() instanceof PreviewItem
            );
        }
    }
}
