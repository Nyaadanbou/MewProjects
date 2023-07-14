package cc.mewcraft.mewfishing.module.fishpower;

import cc.mewcraft.mewcore.progressbar.ProgressbarDisplay;
import cc.mewcraft.mewcore.progressbar.ProgressbarGenerator;
import cc.mewcraft.mewfishing.MewFishing;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.jetbrains.annotations.NotNull;

public class FishPowerModule implements TerminableModule {
    private final MewFishing plugin;
    private final Injector injector;

    public FishPowerModule(final MewFishing plugin) {
        this.plugin = plugin;
        this.injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
                bind(MewFishing.class).toInstance(plugin);
                bind(ProgressbarDisplay.class).toInstance(new ProgressbarDisplay(
                    plugin.config().progressbarStayTime(),
                    ProgressbarGenerator.Builder.builder()
                        .left(plugin.lang().of("cooldown_progressbar.left").plain())
                        .full(plugin.lang().of("cooldown_progressbar.full").plain())
                        .empty(plugin.lang().of("cooldown_progressbar.empty").plain())
                        .right(plugin.lang().of("cooldown_progressbar.right").plain())
                        .width(plugin.config().progressbarWidth())
                        .build()
                ));
            }
        });
    }

    public @NotNull CooldownManager getCooldownManager() {
        return injector.getInstance(CooldownManager.class);
    }

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        if (!plugin.config().fishPowerEnabled()) {
            plugin.log("FishingPower", false);
            return;
        }

        // Register the messenger to sync cooldown across servers
        plugin.registerListener(injector.getInstance(CooldownMessenger.class)).bindWith(consumer);

        // Register the fishing listener
        plugin.registerListener(injector.getInstance(FishingListener.class)).bindWith(consumer);
    }

}
