package com.ranull.proxychatbridge.velocity.command;

import com.ranull.proxychatbridge.velocity.ProxyChatBridge;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Collections;
import java.util.List;

import static net.kyori.adventure.text.Component.text;

public class ProxyChatBridgeCommand implements SimpleCommand {
    private final ProxyChatBridge plugin;

    public ProxyChatBridgeCommand(ProxyChatBridge plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (source.hasPermission("proxychatbridge.command.reload")) {
                plugin.reloadConfig();
                source.sendMessage(text("ProxyChatBridge-Velocity reloaded!").color(NamedTextColor.AQUA));
            }
        }
    }

    @Override public List<String> suggest(final Invocation invocation) {
        if (invocation.arguments().length == 1) {
            return List.of("reload");
        }
        return Collections.emptyList();
    }

    @Override public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("proxychatbridge.command");
    }

}
