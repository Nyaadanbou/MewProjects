package cc.mewcraft.mewfishing.module.autofish;

import cc.mewcraft.mewfishing.MewFishing;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.jetbrains.annotations.NotNull;

public class AutoFishModule implements TerminableModule {
    private final Injector injector;
    private final MewFishing plugin;

    public AutoFishModule(final MewFishing plugin) {
        this.plugin = plugin;
        this.injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
                bind(MewFishing.class).toInstance(plugin);
            }
        });
    }

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        if (!plugin.config().autoFishEnabled()) {
            plugin.log("AutoFishing", false);
            return;
        }

        // Register the auto fishing listener
        plugin.registerListener(injector.getInstance(FishingListener.class)).bindWith(consumer);
    }
}
