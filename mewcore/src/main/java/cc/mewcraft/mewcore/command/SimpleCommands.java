package cc.mewcraft.mewcore.command;

import cloud.commandframework.Command;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * This class makes it easier to prepare and register simple commands.
 */
public abstract class SimpleCommands<P extends Plugin> {
    protected final @NotNull P plugin;
    protected final @NotNull CommandRegistry<P> registry;

    public SimpleCommands(final @NotNull P plugin) throws Exception {
        this.plugin = plugin;
        this.registry = new CommandRegistry<>(plugin);
    }

    /**
     * Implementation Notes:
     * <p>
     * You may use {@link CommandRegistry#prepareCommand(Command)} to prepare commands
     * and {@link CommandRegistry#registerCommands()} to register all prepared commands
     * in this method.
     * <p>
     * This method then can be called upon plugin enabling to register commands with ease!
     * <p>
     * Expected usage:
     * <pre>{@code
     *     public void prepareAndRegister() {
     *         // Prepare commands
     *         registry.prepareCommand(registry
     *             .commandBuilder("home")
     *             .senderType(Player.class)
     *             .handler(ctx -> {
     *                 // Code to teleport home
     *             }).build());
     *         // Register commands
     *         registry.registerCommands();
     *     }
     * }</pre>
     */
    public abstract void prepareAndRegister();
}
