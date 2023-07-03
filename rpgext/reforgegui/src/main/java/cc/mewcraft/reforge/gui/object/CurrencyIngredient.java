package cc.mewcraft.reforge.gui.object;

import me.xanium.gemseconomy.api.Currency;
import me.xanium.gemseconomy.api.GemsEconomy;
import me.xanium.gemseconomy.api.GemsEconomyProvider;

import java.util.Objects;
import java.util.UUID;

public class CurrencyIngredient implements ReforgeIngredient {
    final String identifier; // Identifier of currency
    final double amount; // Required amount

    public CurrencyIngredient(final String identifier, final double amount) {
        this.identifier = identifier;
        this.amount = amount;
    }

    public boolean has(UUID uuid) {
        GemsEconomy econ = GemsEconomyProvider.get();
        Currency currency = Objects.requireNonNull(econ.getCurrency(identifier));
        return econ.getBalance(uuid, currency) >= amount;
    }

    public void consume(UUID uuid) {
        GemsEconomy econ = GemsEconomyProvider.get();
        Currency currency = Objects.requireNonNull(econ.getCurrency(identifier));
        econ.withdraw(uuid, amount, currency);
    }
}