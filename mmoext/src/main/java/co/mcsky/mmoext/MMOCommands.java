package co.mcsky.mmoext;

import me.lucko.helper.Commands;
import org.bukkit.command.ConsoleCommandSender;

import java.util.Locale;

public class MMOCommands {

    private final RPGBridge plugin;

    public MMOCommands(RPGBridge plugin) {
        this.plugin = plugin;
    }

    public void register() {
        Commands.create()
            .assertConsole()
            .handler(context -> {
                context.arg(0).assertPresent();
                String parse = context.arg(0).parseOrFail(String.class).toLowerCase(Locale.ROOT);
                ConsoleCommandSender sender = context.sender();
                if (parse.equals("reload")) {
                    RPGBridge.reload();
                    //noinspection UnstableApiUsage
                    RPGBridge.lang().of("msg_reloaded_config").replace("plugin", plugin.getPluginMeta().getName()).send(sender);
                }
            })
            .registerAndBind(plugin, "mmoext");
    }

}
