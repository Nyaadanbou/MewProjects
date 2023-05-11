package co.mcsky.mmoext;

import me.lucko.helper.Commands;
import org.bukkit.command.ConsoleCommandSender;

import java.util.Locale;

public class MMOCommands {

    private final Main plugin;

    public MMOCommands(Main plugin) {
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
                            Main.reload();
                            sender.sendMessage(Main.lang().getMiniMessage("reloadedConfig"));
                        }
                    }
                })
                .registerAndBind(plugin, "mmoext");
    }

}
