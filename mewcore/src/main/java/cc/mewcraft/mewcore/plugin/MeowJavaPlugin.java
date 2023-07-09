package cc.mewcraft.mewcore.plugin;

import cc.mewcraft.mewcore.util.UtilFile;
import me.lucko.helper.Schedulers;
import me.lucko.helper.Services;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import me.lucko.helper.terminable.module.TerminableModule;
import me.lucko.helper.utils.CommandMapUtil;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MeowJavaPlugin extends JavaPlugin implements MeowPlugin {
    // the backing terminable registry
    private CompositeTerminable terminableRegistry;

    // Used by subclasses to perform logic for plugin load/enable/disable.
    protected void load() {}

    protected void enable() {}

    protected void disable() {}

    @Override
    public final void onLoad() {
        this.terminableRegistry = CompositeTerminable.create();

        // call subclass
        load();
    }

    @Override
    public final void onEnable() {
        // schedule cleanup of the registry
        Schedulers.builder()
            .async()
            .after(10, TimeUnit.SECONDS)
            .every(30, TimeUnit.SECONDS)
            .run(this.terminableRegistry::cleanup)
            .bindWith(this.terminableRegistry);

        // call subclass
        enable();
    }

    @Override
    public final void onDisable() {

        // call subclass
        disable();

        // terminate the registry
        this.terminableRegistry.closeAndReportException();
    }

    @Override public @NotNull <T extends AutoCloseable> T bind(@NotNull T terminable) {
        return this.terminableRegistry.bind(terminable);
    }

    @Override public @NotNull <T extends TerminableModule> T bindModule(@NotNull T module) {
        return this.terminableRegistry.bindModule(module);
    }

    @Override public @NotNull <T extends Listener> T registerListener(@NotNull T listener) {
        Objects.requireNonNull(listener, "listener");
        getServer().getPluginManager().registerEvents(listener, this);
        return listener;
    }

    @Override public @NotNull <T extends CommandExecutor> T registerCommand(@NotNull T command, String permission, String permissionMessage, String description, @NotNull String... aliases) {
        return CommandMapUtil.registerCommand(this, command, permission, permissionMessage, description, aliases);
    }

    @Override public @NotNull <T> T getService(@NotNull Class<T> service) {
        return Services.load(service);
    }

    @Override public @NotNull <T> T provideService(@NotNull Class<T> clazz, @NotNull T instance, @NotNull ServicePriority priority) {
        return Services.provide(clazz, instance, this, priority);
    }

    @Override public @NotNull <T> T provideService(@NotNull Class<T> clazz, @NotNull T instance) {
        return provideService(clazz, instance, ServicePriority.Normal);
    }

    @Override
    public boolean isPluginPresent(@NotNull String name) {
        return getServer().getPluginManager().getPlugin(name) != null;
    }

    @SuppressWarnings("unchecked")
    @Override public @Nullable <T> T getPlugin(@NotNull String name, @NotNull Class<T> pluginClass) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(pluginClass, "pluginClass");
        return (T) getServer().getPluginManager().getPlugin(name);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File getRelativeFile(@NotNull String name) {
        getDataFolder().mkdirs();
        return new File(getDataFolder(), name);
    }

    @Override public @NotNull File getBundledFile(@NotNull String name) {
        Objects.requireNonNull(name, "name");
        File file = getRelativeFile(name);
        if (!file.exists()) {
            saveResource(name, false);
        }
        return file;
    }

    @Override public @NotNull YamlConfiguration loadConfig(@NotNull String file) {
        Objects.requireNonNull(file, "file");
        return YamlConfiguration.loadConfiguration(getBundledFile(file));
    }

    @Override public void saveResourceRecursively(final @NotNull String name) {
        saveResourceRecursively(name, false);
    }

    @Override public void saveResourceRecursively(final @NotNull String name, final boolean overwrite) {
        Objects.requireNonNull(name, "name");
        File targetFile = new File(getDataFolder(), name);
        if (overwrite || !targetFile.exists()) {
            UtilFile.copyResourcesRecursively(getClassLoader().getResource(name), targetFile);
        }
    }

    @Override public @NotNull ClassLoader getClazzLoader() {
        return super.getClassLoader();
    }
}
