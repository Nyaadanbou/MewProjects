package com.ranull.proxychatbridge.velocity.manager;

import com.google.common.io.ByteStreams;
import com.ranull.proxychatbridge.velocity.ProxyChatBridge;
import com.velocitypowered.api.proxy.server.ServerInfo;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class ConfigManager {

    private final ProxyChatBridge plugin;
    private ConfigurationNode root;

    public ConfigManager(ProxyChatBridge plugin) {
        this.plugin = plugin;
        saveConfig();
        loadConfig();
    }

    public ConfigurationNode getConfig() {
        return root;
    }

    public void saveConfig() {
        // noinspection ResultOfMethodCallIgnored
        plugin.getDataFolder().mkdirs(); // velocity doesn't automatically create the data folder for us

        try (InputStream in = getClass().getClassLoader().getResourceAsStream("rules.yml")) {
            File config = new File(plugin.getDataFolder(), "rules.yml");
            if (config.createNewFile()) {
                ByteStreams.copy(requireNonNull(in), Files.newOutputStream(config.toPath()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadConfig() {
        try {
            root = YAMLConfigurationLoader.builder()
                .setIndent(2)
                .setFile(new File(plugin.getDataFolder(), "rules.yml"))
                .build()
                .load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- Getters ---

    public boolean skipEmpty() {
        return getConfig().getNode("skip-empty").getBoolean();
    }

    public @NotNull String getGroupValue(String groupKey) {
        return getConfig().getNode("group-mappings", groupKey).getString("");
    }

    public boolean isDisabled(@NotNull ServerInfo serverInfo) {
        return isDisabled(serverInfo.getName());
    }

    public boolean isDisabled(@NotNull String serverName) {
        return getConfig().getNode("servers", serverName).isEmpty();
    }

    public @NotNull String getServerGroup(@NotNull ServerInfo serverInfo) {
        return getServerGroup(serverInfo.getName());
    }

    public @NotNull String getServerGroup(@NotNull String serverName) {
        String group = Optional
            .ofNullable(getConfig().getNode("servers", serverName, "group").getString())
            .orElseGet(() -> getConfig().getNode("default", "group").getString());
        return requireNonNull(group);
    }

    public @NotNull String getMessageFormat(@NotNull ServerInfo serverInfo) {
        return getMessageFormat(serverInfo.getName());
    }

    public @NotNull String getMessageFormat(@NotNull String serverName) {
        String format = Optional
            .ofNullable(getConfig().getNode("servers", serverName, "format").getString())
            .orElseGet(() -> getConfig().getNode("default", "format").getString());
        return requireNonNull(format);
    }

}
