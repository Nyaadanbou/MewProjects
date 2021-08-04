package co.mcsky.moecore;

import co.mcsky.moecore.config.YamlConfigFactory;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;

public final class MoeConfig {
    public static final String FILENAME = "config.yml";

    /* config nodes start */

    public boolean debug;

    /* config nodes end */

    private YamlConfigurationLoader loader;
    private CommentedConfigurationNode root;

    public MoeConfig() {
        loader = YamlConfigFactory.loader(new File(MoeCore.plugin.getDataFolder(), FILENAME));
    }

    public void load() {
        try {
            root = loader.load();
        } catch (ConfigurateException e) {
            MoeCore.plugin.getLogger().severe(e.getMessage());
            MoeCore.plugin.getServer().getPluginManager().disablePlugin(MoeCore.plugin);
        }

        /* initialize config nodes */

        debug = root.node("debug").getBoolean(true);
    }

    public void save() {
        try {
            loader.save(root);
        } catch (ConfigurateException e) {
            MoeCore.plugin.getLogger().severe(e.getMessage());
        }
    }

}
