package com.ranull.proxychatbridge.bukkit.command;

import com.ranull.proxychatbridge.bukkit.ProxyChatBridge;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReloadCommand implements TabExecutor {

    private final ProxyChatBridge plugin;

    public ReloadCommand(final ProxyChatBridge plugin) {
        this.plugin = plugin;
    }

    @Override public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, final @NotNull String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            sender.sendMessage(Component.text("ProxyChatBridge-Bukkit reloaded!").color(NamedTextColor.AQUA));
            return true;
        }
        return false;
    }

    @Override public @Nullable List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, final @NotNull String[] args) {
        if (args.length == 1) {
            String input = args[0];
            List<String> completions = new ArrayList<>();
            StringUtil.copyPartialMatches(input, List.of("reload"), completions);
            return completions;
        }
        return Collections.emptyList();
    }

}
