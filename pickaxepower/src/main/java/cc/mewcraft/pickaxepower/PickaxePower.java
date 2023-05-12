package cc.mewcraft.pickaxepower;

import cc.mewcraft.pickaxepower.listener.PacketListener;
import cc.mewcraft.pickaxepower.listener.PlayerListener;
import cloud.commandframework.Command;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class PickaxePower extends ExtendedJavaPlugin {

    private CommandRegistry commandRegistry;

    @Override
    protected void enable() {
        this.saveDefaultConfig();
        this.reloadConfig();
        this.saveResource("blocks.yml");
        this.saveResource("pickaxes.yml");

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
                this.bind(JavaPlugin.class).toInstance(PickaxePower.this);
                this.bind(PickaxePower.class).toInstance(PickaxePower.this);
                this.bind(LoreWriter.class).to(LoreWriterImpl.class);
                this.bind(PowerResolver.class).to(PowerResolverImpl.class);
            }
        });

        PacketListener packetListener = this.bind(injector.getInstance(PacketListener.class));
        PlayerListener playerListener = this.bind(injector.getInstance(PlayerListener.class));
        this.registerListener(playerListener);

        try {
            commandRegistry = new CommandRegistry(this);
            Command<CommandSender> reloadCommand = commandRegistry.commandBuilder("pickaxepower")
                .literal("reload")
                .permission("pickaxepower.command.reload")
                .handler(context -> {
                    this.onDisable();
                    this.onEnable();
                    context.getSender().sendRichMessage("<aqua>" + this.getName() + " has been reloaded!");
                })
                .build();
            commandRegistry.prepareCommand(reloadCommand);
            commandRegistry.registerCommands();
        } catch (Exception e) {
            e.printStackTrace();
            this.getSLF4JLogger().error("Failed to register commands!");
        }
    }

    @Override
    protected void disable() {

    }

    private void saveResource(String path) {
        if (!this.getDataFolder().toPath().resolve(path).toFile().exists())
            this.saveResource(path, false);
    }
}
