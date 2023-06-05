package com.ranull.proxychatbridge.bukkit.handler;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.ranull.proxychatbridge.bukkit.ProxyChatBridge;
import net.gauntletmc.adventure.serializer.binary.BinaryComponentSerializer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.UUID;

public class MessageHandler {
    private final ProxyChatBridge plugin;

    public MessageHandler(ProxyChatBridge plugin) {
        this.plugin = plugin;
    }

    /**
     * Forward the local message to the proxy.
     */
    public void handleOutgoingMessage(@NotNull Player player, @NotNull Component message) {
        @SuppressWarnings("UnstableApiUsage")
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF("Global");
        out.writeUTF(player.getUniqueId().toString());
        try {
            byte[] componentBytes = BinaryComponentSerializer.INSTANCE.serialize(message);
            out.writeInt(componentBytes.length); // store length of the byte array
            out.write(componentBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        player.sendPluginMessage(plugin, ProxyChatBridge.PLUGIN_MESSAGE_CHANNEL, out.toByteArray());
    }

    /**
     * Forward the proxy message to all the players in this server.
     */
    public void handleIncomingMessage(final byte @NotNull [] data) {
        @SuppressWarnings("UnstableApiUsage")
        ByteArrayDataInput in = ByteStreams.newDataInput(data);

        String channel = in.readUTF();
        if (!channel.equals("Global")) return;
        UUID player = UUID.fromString(in.readUTF());
        try {
            int messageLength = in.readInt();
            byte[] componentBytes = new byte[messageLength];
            in.readFully(componentBytes);
            Component message = BinaryComponentSerializer.INSTANCE.deserialize(componentBytes);

            Bukkit.getServer().sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
