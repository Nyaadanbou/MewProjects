package cc.mewcraft.reforge.gui.util;

import net.kyori.adventure.text.Component;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;

public final class AdventureUtils {
    public static AdventureComponentWrapper translatable(String key) {
        return new AdventureComponentWrapper(Component.translatable(key));
    }

    private AdventureUtils() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
}
