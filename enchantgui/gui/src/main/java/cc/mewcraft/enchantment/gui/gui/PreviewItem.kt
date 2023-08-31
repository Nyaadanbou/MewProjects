package cc.mewcraft.enchantment.gui.gui;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.CycleItem;

import java.util.function.BiConsumer;

/**
 * This class is just a representation of enchantment preview item.
 */
public class PreviewItem extends CycleItem {
    public PreviewItem(final int startState, final ItemProvider[] states) {
        super(startState, states);
    }

    // The two static methods are copied from CycleItem with the difference
    // in that the return type is changed to PreviewItem instead of CycleItem

    public static PreviewItem withStateChangeHandler(BiConsumer<Player, Integer> stateChangeHandler, @NotNull ItemProvider... states) {
        return withStateChangeHandler(stateChangeHandler, 0, states);
    }

    public static PreviewItem withStateChangeHandler(BiConsumer<Player, Integer> stateChangeHandler, int startState, @NotNull ItemProvider... states) {
        return new PreviewItem(startState, states) {
            @Override
            protected void handleStateChange(@Nullable Player player, int state) {
                stateChangeHandler.accept(player, state);
            }
        };
    }
}
