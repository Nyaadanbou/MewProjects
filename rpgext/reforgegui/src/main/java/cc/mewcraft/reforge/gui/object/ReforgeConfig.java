package cc.mewcraft.reforge.gui.object;

import cc.mewcraft.mewcore.item.api.PluginItem;
import cc.mewcraft.mewcore.item.api.PluginItemRegistry;
import cc.mewcraft.reforge.gui.ReforgePlugin;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class ReforgeConfig {
    private final ReforgePlugin plugin;
    private final Map<String, List<ReforgeIngredient>> ingredientMap;

    @Inject
    public ReforgeConfig(final ReforgePlugin plugin) {
        this.plugin = plugin;
        this.ingredientMap = new HashMap<>();

        // Load all reforge ingredient config files
        Collection<File> files = FileUtils.listFiles(new File(plugin.getDataFolder(), "items"), new String[]{"yml"}, true);
        PluginItemRegistry itemRegistry = PluginItemRegistry.get();
        Pattern currencyIngredientPattern = Pattern.compile("(\\$)(\\d*\\.?\\d*)(\\D+)");

        for (final File file : files) {
            YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(file);
            Set<String> itemKeys = yamlConfig.getKeys(false);

            for (final String itemKey : itemKeys) {
                PluginItem<?> pi = itemRegistry.fromReferenceNullable(itemKey);
                if (pi == null) {
                    plugin.getSLF4JLogger().error("Unknown item from key: {}", itemKey);
                    continue;
                }

                List<String> rawIngredients = yamlConfig.getStringList(itemKey);
                for (final String rawIngredient : rawIngredients) {
                    if (rawIngredient.startsWith("$")) {
                        // It's an economy currency ingredient
                        Matcher matcher = currencyIngredientPattern.matcher(rawIngredient);
                        if (!matcher.matches()) {
                            plugin.getSLF4JLogger().error("Illegal currency ingredient: {}", rawIngredient);
                            continue;
                        }
                        double amount = Double.parseDouble(matcher.group(2));
                        String identifier = matcher.group(3);
                        ingredientMap.computeIfAbsent(itemKey, k -> new ArrayList<>()).add(new CurrencyIngredient(identifier, amount));

                    } else {
                        // It's an item stack ingredient

                        String[] parts = rawIngredient.split("/");
                        if (parts.length > 2) {
                            plugin.getSLF4JLogger().error("Illegal item stack ingredient: {}", rawIngredient);

                        } else if (parts.length == 2) {
                            PluginItem<?> item = itemRegistry.fromReferenceNullable(parts[0]);
                            int amount = Integer.parseInt(parts[1]);
                            ingredientMap.computeIfAbsent(itemKey, k -> new ArrayList<>()).add(new ItemStackIngredient(item, amount));

                        } else if (parts.length == 1) {
                            PluginItem<?> item = itemRegistry.fromReferenceNullable(parts[0]);
                            ingredientMap.computeIfAbsent(itemKey, k -> new ArrayList<>()).add(new ItemStackIngredient(item));

                        } else {
                            plugin.getSLF4JLogger().error("Illegal item stack ingredient: {}", rawIngredient);
                        }
                    }
                }
            }
        }
    }

    public boolean canReforge(final @NotNull ItemStack test) {
        PluginItem<?> item = PluginItemRegistry.get().fromItemStackNullable(test);
        return item != null && ingredientMap.containsKey(item.asReference());
    }

    public @NotNull List<ReforgeIngredient> getIngredients(final @NotNull ItemStack key) {
        PluginItem<?> item = PluginItemRegistry.get().fromItemStack(key);
        return ingredientMap.get(item.asReference());
    }
}
