package cc.mewcraft.pickaxepower;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * A record encapsulating the power and related data.
 *
 * @param power the power of item/block
 * @param key   the name of item/block
 */
public record PowerData(int power, @NotNull String key) {

    public Component powerComponent() {
        return Component.text(power);
    }

    public Component nameLiteralComponent() {
        return Component.text(key);
    }

    public Component nameTranslatableComponent() {
        return Component.translatable(key);
    }

}
