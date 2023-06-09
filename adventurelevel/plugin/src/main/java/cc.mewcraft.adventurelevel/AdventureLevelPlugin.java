package cc.mewcraft.adventurelevel;

import cc.mewcraft.adventurelevel.command.CommandManager;
import cc.mewcraft.adventurelevel.data.PlayerDataManager;
import cc.mewcraft.adventurelevel.file.DataStorage;
import cc.mewcraft.adventurelevel.hooks.luckperms.LevelContextCalculator;
import cc.mewcraft.adventurelevel.hooks.placeholder.MiniPlaceholderExpansion;
import cc.mewcraft.adventurelevel.hooks.placeholder.PAPIPlaceholderExpansion;
import cc.mewcraft.adventurelevel.listener.PickupExpListener;
import cc.mewcraft.adventurelevel.listener.UserdataListener;
import cc.mewcraft.adventurelevel.message.DataSyncMessenger;
import cc.mewcraft.mewcore.message.Translations;
import cc.mewcraft.mewcore.util.UtilFile;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
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
                bind(AdventureLevelPlugin.class).toInstance(AdventureLevelPlugin.this);

                // these classes are external, we can't use JIT bindings
                bind(Redis.class).toInstance(getService(Redis.class));
                bind(Messenger.class).toInstance(getService(Redis.class));
            }
        });

        // Copy default config files
        saveDefaultConfig();
        if (!getDataFolder().toPath().resolve("categories").toFile().exists()) {
            UtilFile.copyResourcesRecursively(
                Objects.requireNonNull(getClassLoader().getResource("categories")),
                getDataFolder().toPath().resolve("categories").toFile()
            );
        }

        translations = new Translations(this, "languages");

        dataStorage = bind(injector.getInstance(DataStorage.class));
        dataStorage.init();

        playerDataManager = bind(injector.getInstance(PlayerDataManager.class));
        dataSyncMessenger = bind(injector.getInstance(DataSyncMessenger.class));

        // Register placeholders
        injector.getInstance(MiniPlaceholderExpansion.class).register().bindWith(this);
        injector.getInstance(PAPIPlaceholderExpansion.class).register().bindWith(this);

        // Register listeners
        registerListener(injector.getInstance(PickupExpListener.class)).bindWith(this);
        registerListener(injector.getInstance(UserdataListener.class)).bindWith(this);

        // Register LuckPerms contexts
        injector.getInstance(LevelContextCalculator.class).register();

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
