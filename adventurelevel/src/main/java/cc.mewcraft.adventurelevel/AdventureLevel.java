package cc.mewcraft.adventurelevel;

import cc.mewcraft.adventurelevel.data.PlayerDataManager;
import cc.mewcraft.adventurelevel.data.PlayerDataManagerImpl;
import cc.mewcraft.adventurelevel.file.DataStorage;
import cc.mewcraft.adventurelevel.file.SQLDataStorage;
import cc.mewcraft.adventurelevel.listener.PickupExpListener;
import cc.mewcraft.adventurelevel.listener.UserdataListener;
import cc.mewcraft.adventurelevel.placeholder.MiniPlaceholderExpansion;
import cc.mewcraft.adventurelevel.placeholder.PAPIPlaceholderExpansion;
import cc.mewcraft.mewcore.util.UtilFile;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AdventureLevel extends ExtendedJavaPlugin {
    private static @MonotonicNonNull AdventureLevel INSTANCE;

    private @MonotonicNonNull Injector injector;
    private @MonotonicNonNull DataStorage dataStorage;
    private @MonotonicNonNull PlayerDataManager playerDataManager;

    public static @NotNull AdventureLevel getInstance() {
        return INSTANCE;
    }

    @Override protected void enable() {
        INSTANCE = this;

        injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
                // plugin instance
                this.bind(AdventureLevel.class).toInstance(AdventureLevel.this);

                // listeners
                this.bind(UserdataListener.class).in(Singleton.class);
                this.bind(PickupExpListener.class).in(Singleton.class);

                // data manager
                this.bind(DataStorage.class).to(SQLDataStorage.class).in(Singleton.class);
                this.bind(PlayerDataManager.class).to(PlayerDataManagerImpl.class).in(Singleton.class);

                // placeholder expansions
                this.bind(MiniPlaceholderExpansion.class).in(Singleton.class);
                this.bind(PAPIPlaceholderExpansion.class).in(Singleton.class);
            }
        });

        // Copy default config files
        this.saveDefaultConfig();
        UtilFile.copyResourcesRecursively(
            Objects.requireNonNull(this.getClassLoader().getResource("categories")),
            this.getDataFolder().toPath().resolve("categories").toFile()
        );

        dataStorage = injector.getInstance(DataStorage.class);
        dataStorage.bindWith(this);
        dataStorage.init();

        playerDataManager = injector.getInstance(PlayerDataManager.class);
        playerDataManager.bindWith(this);

        injector.getInstance(MiniPlaceholderExpansion.class).register();
        injector.getInstance(PAPIPlaceholderExpansion.class).register();

        this.registerListener(injector.getInstance(PickupExpListener.class)).bindWith(this);
        this.registerListener(injector.getInstance(UserdataListener.class)).bindWith(this);
    }

    @Override protected void disable() {

    }

    public @NotNull PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public @NotNull DataStorage getDataStorage() {
        return dataStorage;
    }
}
