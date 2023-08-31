package cc.mewcraft.enchantment.gui.api;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;

/**
 * A UiEnchant that is chargeable.
 */
public class ChargeableUiEnchant extends UiEnchantDecorator {
    final String fuel;
    final Map<Integer, Integer> fuelConsume;
    final Map<Integer, Integer> fuelRecharge;
    final Map<Integer, Integer> maximumFuel;

    public ChargeableUiEnchant(
        final UiEnchant decoratedEnchantment,
        final String fuel,
        final Function<Integer, Integer> fuelConsume,
        final Function<Integer, Integer> fuelRecharge,
        final Function<Integer, Integer> maximumFuel
    ) {
        super(decoratedEnchantment);
        this.fuel = fuel;
        this.fuelConsume = scaleMapper(fuelConsume);
        this.fuelRecharge = scaleMapper(fuelRecharge);
        this.maximumFuel = scaleMapper(maximumFuel);
    }

    /**
     * @return name of the fuel item in MiniMessage string representation
     */
    public @NotNull String fuel() {
        return fuel;
    }

    public @NotNull Map<@NotNull Integer, @NotNull Integer> fuelConsume() {
        return fuelConsume;
    }

    public @NotNull Map<@NotNull Integer, @NotNull Integer> fuelRecharge() {
        return fuelRecharge;
    }

    public @NotNull Map<@NotNull Integer, @NotNull Integer> maximumFuel() {
        return maximumFuel;
    }
}
