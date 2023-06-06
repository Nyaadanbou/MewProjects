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
                    switch (parse) {
                        case "reload" -> {
                            RPGBridge.reload();
                            sender.sendMessage(RPGBridge.lang().getMiniMessage("reloadedConfig"));
                        }
                    }
                })
                .registerAndBind(plugin, "mmoext");
    }

}
