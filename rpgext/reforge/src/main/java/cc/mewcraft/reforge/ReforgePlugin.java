package cc.mewcraft.reforge;

import cc.mewcraft.reforge.api.ReforgeProvider;
import cc.mewcraft.reforge.hook.MMOItemsReforge;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import me.lucko.helper.plugin.ExtendedJavaPlugin;

public class ReforgePlugin extends ExtendedJavaPlugin {
    @Override protected void enable() {
        saveDefaultConfig();
        reloadConfig();

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
                bind(ReforgePlugin.class).toInstance(ReforgePlugin.this);
            }
        });

        if (isPluginPresent("MMOItems")) {
            ReforgeProvider.register(injector.getInstance(MMOItemsReforge.class));
            getSLF4JLogger().info("Registered reforge provider: MMOItems");
        } else {
            getSLF4JLogger().error("Registered reforge provider: NONE");
        }
    }

    @Override protected void disable() {
        ReforgeProvider.unregister();
        getSLF4JLogger().info("Unregistered reforge provider");
    }
}
