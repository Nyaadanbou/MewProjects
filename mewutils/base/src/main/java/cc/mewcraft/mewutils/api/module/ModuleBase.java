package cc.mewcraft.mewutils.api.module;

import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import cc.mewcraft.mewcore.message.Translations;
import cc.mewcraft.mewcore.util.UtilFile;
import cc.mewcraft.mewutils.api.MewPlugin;
import cc.mewcraft.mewutils.api.command.CommandRegistry;
import cloud.commandframework.Command;
import com.google.inject.Inject;
import me.lucko.helper.Schedulers;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import me.lucko.helper.terminable.module.TerminableModule;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.text;

@DefaultQualifier(NonNull.class)
public abstract class ModuleBase
    implements TerminableConsumer, ModuleLogger, ModuleRequirement {

    private final MewPlugin plugin;
    private final Path moduleDirectory;
    private final CompositeTerminable terminableRegistry;
    private final YamlConfigurationLoader config;
    private @MonotonicNonNull CommentedConfigurationNode configNode;
    private @MonotonicNonNull Translations lang;
    private boolean moduleOn;

    @Inject
    public ModuleBase(MewPlugin parent) {
        this.plugin = parent;

        // backed closeable of this module
        this.terminableRegistry = CompositeTerminable.create();

        // create dedicated directory for this module
        this.moduleDirectory = this.plugin.getDataFolder().toPath().resolve("modules").resolve(getId());
        if (this.moduleDirectory.toFile().mkdirs())
            info("module directory does not exist - creating one");

        // dedicated config file for this module
        File configFile = this.moduleDirectory.resolve("config.yml").toFile();
        if (!configFile.exists()) {
            // copy default config.yml if not existing
            UtilFile.copyResourcesRecursively(parent.getClassLoader0().getResource("modules/" + getId() + "/config.yml"), configFile);
        }
        this.config = YamlConfigurationLoader.builder()
            .file(configFile)
            .indent(2)
            .build();
    }

    /**
     * This runs before {@link #enable()}. Initialisation should be done here.
     *
     * @throws Exception any exceptions
     */
    protected void load() throws Exception {}

    /**
     * This runs after {@link #load()}
     *
     * @throws Exception any exceptions
     */
    protected void enable() throws Exception {}

    /**
     * This runs after the console prints "Done!", which means all the plugins are loaded at that time. It's useful if
     * you only, for example, need to register listeners of other plugins.
     *
     * @throws Exception any exceptions
     */
    protected void postLoad() throws Exception {}

    /**
     * This runs when the server shutdown.
     *
     * @throws Exception any exceptions
     */
    protected void disable() throws Exception {}

    public final void onLoad() throws Exception {
        // load the config file into node
        this.configNode = this.config.load();

        // dedicated language files for this module
        Path langDirectory = Path.of("modules").resolve(getId()).resolve("lang");
        if (this.plugin.getDataFolder().toPath().resolve(langDirectory).toFile().mkdirs())
            info("translation directory does not exist - creating one");
        this.lang = new Translations(this.plugin, langDirectory.toString(), "zh");

        // call subclass
        load();
    }

    public final void onEnable() throws Exception {
        if (!checkRequirement()) {
            warn(getLongId() + " is not enabled due to requirement not met");
            return;
        }

        // schedule cleanup of the registry
        Schedulers.builder()
            .async()
            .after(10, TimeUnit.SECONDS)
            .every(30, TimeUnit.SECONDS)
            .run(this.terminableRegistry::cleanup)
            .bindWith(this.terminableRegistry);

        // call subclass
        enable();

        this.plugin.getComponentLogger().info(text()
            .append(text(getLongId()).color(NamedTextColor.GOLD))
            .appendSpace().append(text("is enabled!"))
            .build()
        );

        Schedulers.bukkit().runTask(this.plugin, () -> {
            try {
                postLoad();
            } catch (Throwable e) {
                this.plugin.getLogger().severe("Errors occurred in postLoad()");
                e.printStackTrace();
            }
        });
    }

    public final void onDisable() throws Exception {
        // call subclass
        disable();

        // terminate the registry
        this.terminableRegistry.closeAndReportException();

        this.plugin.getComponentLogger().info(text()
            .append(text(getLongId()).color(NamedTextColor.GOLD))
            .appendSpace().append(text("is disabled!"))
            .build()
        );
    }

    public final Translations getLang() {
        return this.lang;
    }

    public final YamlConfigurationLoader getConfigLoader() {
        return this.config;
    }

    public final CommentedConfigurationNode getConfigNode() {
        return requireNonNull(this.configNode);
    }

    public final Path getDataFolder() {
        return this.moduleDirectory.resolve("data");
    }

    public final Path getModuleFolder() {
        return this.moduleDirectory;
    }

    public final <T extends AutoCloseableListener> void registerListener(@NonNull T listener) {
        requireNonNull(listener, "cc/mewcraft/mewcore/listener");
        getParentPlugin().getServer().getPluginManager().registerEvents(
            bind(listener), getParentPlugin()
        );
    }

    public final void registerCommand(@NonNull Function<CommandRegistry, Command.Builder<CommandSender>> command) {
        requireNonNull(command, "command");
        CommandRegistry registry = getParentPlugin().getCommandRegistry();
        registry.prepareCommand(
            command.apply(registry).build()
        );
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T getPlugin(@NonNull String name, @NonNull Class<T> pluginClass) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(pluginClass, "pluginClass");
        return (T) Bukkit.getServer().getPluginManager().getPlugin(name);
    }

    public final boolean isPluginPresent(String name) {
        return Bukkit.getServer().getPluginManager().getPlugin(name) != null;
    }

    @Override
    public final <T extends TerminableModule> @NonNull T bindModule(@NonNull final T module) {
        requireNonNull(module, "module");
        return this.terminableRegistry.bindModule(module);
    }

    @Override
    public final <T extends AutoCloseable> @NonNull T bind(@NonNull final T terminable) {
        requireNonNull(terminable, "terminable");
        return this.terminableRegistry.bind(terminable);
    }

    @Override
    public final MewPlugin getParentPlugin() {
        return this.plugin;
    }

}
