package cc.mewcraft.mewfishing.module.fishloot;

import cc.mewcraft.mewfishing.MewFish;
import cc.mewcraft.mewfishing.loot.LootTableManager;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.jetbrains.annotations.NotNull;

public class FishLootModule implements TerminableModule {
    private final Injector injector;
    private final MewFish plugin;
    private final LootTableManager lootTableManager;

    public FishLootModule(final MewFish plugin) {
        this.plugin = plugin;
        this.injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
                bind(MewFish.class).toInstance(plugin);
            }
        });

        this.lootTableManager = injector.getInstance(LootTableManager.class);
    }

    @Override public void setup(final @NotNull TerminableConsumer consumer) {
        plugin.registerListener(injector.getInstance(FishingListener.class)).bindWith(consumer);
    }

    public LootTableManager getLootTableManager() {
        return lootTableManager;
    }
}
