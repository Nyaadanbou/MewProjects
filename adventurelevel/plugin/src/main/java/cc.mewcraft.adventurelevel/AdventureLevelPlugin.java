package cc.mewcraft.adventurelevel;

import cc.mewcraft.adventurelevel.command.CommandManager;
import cc.mewcraft.adventurelevel.data.PlayerDataManager;
import cc.mewcraft.adventurelevel.data.PlayerDataManagerImpl;
import cc.mewcraft.adventurelevel.file.DataStorage;
import cc.mewcraft.adventurelevel.file.SQLDataStorage;
import cc.mewcraft.adventurelevel.listener.PickupExpListener;
import cc.mewcraft.adventurelevel.listener.UserdataListener;
import cc.mewcraft.adventurelevel.message.DataSyncMessenger;
import cc.mewcraft.adventurelevel.placeholder.MiniPlaceholderExpansion;
import cc.mewcraft.adventurelevel.placeholder.PAPIPlaceholderExpansion;
import cc.mewcraft.mewcore.message.Translations;
import cc.mewcraft.mewcore.util.UtilFile;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.lucko.helper.messaging.Messenger;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.redis.Redis;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AdventureLevelPlugin extends ExtendedJavaPlugin implements AdventureLevel {
    private static @MonotonicNonNull AdventureLevelPlugin INSTANCE;

    private @MonotonicNonNull Injector injector;
    private @MonotonicNonNull DataStorage dataStorage;
    private @MonotonicNonNull DataSyncMessenger dataSyncMessenger;
    private @MonotonicNonNull PlayerDataManager playerDataManager;
    private @MonotonicNonNull Translations translations;

    public static @NotNull AdventureLevelPlugin getInstance() {
        return INSTANCE;
    }

    @Override protected void enable() {
        INSTANCE = this;

        // Register API instance
        AdventureLevelProvider.register(this);

        injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
                // plugin instance
                bind(AdventureLevelPlugin.class).toInstance(AdventureLevelPlugin.this);

                // listeners
                bind(UserdataListener.class).in(Singleton.class);
                bind(PickupExpListener.class).in(Singleton.class);

                // data manager
                bind(DataStorage.class).to(SQLDataStorage.class).in(Singleton.class);
                bind(PlayerDataManager.class).to(PlayerDataManagerImpl.class).in(Singleton.class);

                // data messenger
                bind(Messenger.class).toInstance(getService(Redis.class));
                bind(DataSyncMessenger.class).in(Singleton.class);

                // placeholder expansions
                bind(MiniPlaceholderExpansion.class).in(Singleton.class);
                bind(PAPIPlaceholderExpansion.class).in(Singleton.class);
            }
        });

        // Copy default config files
        saveDefaultConfig();
        UtilFile.copyResourcesRecursively(
            Objects.requireNonNull(getClassLoader().getResource("categories")),
            getDataFolder().toPath().resolve("categories").toFile()
        );

        translations = new Translations(this, "languages");

        dataStorage = injector.getInstance(DataStorage.class);
        dataStorage.bindWith(this);
        dataStorage.init();

        playerDataManager = injector.getInstance(PlayerDataManager.class);
        playerDataManager.bindWith(this);

        dataSyncMessenger = injector.getInstance(DataSyncMessenger.class);
        dataSyncMessenger.bindWith(this);

        injector.getInstance(MiniPlaceholderExpansion.class).register().bindWith(this);
        injector.getInstance(PAPIPlaceholderExpansion.class).register().bindWith(this);

        registerListener(injector.getInstance(PickupExpListener.class)).bindWith(this);
        registerListener(injector.getInstance(UserdataListener.class)).bindWith(this);

        try {
            new CommandManager(this);
        } catch (Exception e) {
            getLogger().severe("Failed to initialise commands!");
            e.printStackTrace();
        }
    }

    @Override protected void disable() {

    }

    public @NotNull DataStorage getDataStorage() {
        return dataStorage;
    }

    public @NotNull PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public @NotNull DataSyncMessenger getPlayerDataMessenger() {
        return dataSyncMessenger;
    }

    public @NotNull Translations getLang() {
        return translations;
    }
}
