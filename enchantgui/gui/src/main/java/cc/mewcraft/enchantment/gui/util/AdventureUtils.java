package cc.mewcraft.enchantment.gui.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;

public final class AdventureUtils {
    public static ComponentWrapper translatable(String key) {
        return new AdventureComponentWrapper(Component.translatable(key));
    }

    public static ComponentWrapper miniMessage(String miniMessage) {
        return new AdventureComponentWrapper(MiniMessage.miniMessage().deserialize(miniMessage));
    }

    private AdventureUtils() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
}
