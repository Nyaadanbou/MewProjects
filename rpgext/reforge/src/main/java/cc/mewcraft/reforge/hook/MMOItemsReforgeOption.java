package cc.mewcraft.reforge.hook;

import cc.mewcraft.reforge.ReforgePlugin;
import cc.mewcraft.reforge.api.ReforgeOption;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.Indyuce.mmoitems.api.ReforgeOptions;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Singleton
public class MMOItemsReforgeOption implements ReforgeOption {
    private final ReforgePlugin plugin;
    private final Map<String, ReforgeOptions> config;

    @Inject
    public MMOItemsReforgeOption(final ReforgePlugin plugin) {
        this.plugin = plugin;
        this.config = new HashMap<>();

        // Load options from config files
        ConfigurationSection section = Objects.requireNonNull(plugin.getConfig().getConfigurationSection("reforge_options.mmoitems"));
        for (final String key : section.getKeys(false)) {
            String optionData = Objects.requireNonNull(section.getString(key));
            boolean[] mask = new boolean[12];
            for (final String n : optionData.split(",")) {
                int set = Integer.parseInt(String.valueOf(n));
                mask[set - 1] = true;
            }
            config.put(key, new ReforgeOptions(mask));
        }
    }

    public @Nullable ReforgeOptions parse(String optionKey) {
        return config.get(optionKey);
    }
}
