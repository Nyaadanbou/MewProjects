package cc.mewcraft.townylink.messager;

import cc.mewcraft.townylink.TownyLink;
import cc.mewcraft.townylink.object.TownyRepository;
import cc.mewcraft.townylink.util.TownyUtils;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import de.themoep.connectorplugin.bukkit.BukkitConnectorPlugin;
import de.themoep.connectorplugin.connector.ConnectingPlugin;
import de.themoep.connectorplugin.connector.Message;
import de.themoep.connectorplugin.connector.MessageTarget;
import me.lucko.helper.Schedulers;
import me.lucko.helper.terminable.Terminable;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

@SuppressWarnings("UnstableApiUsage")
public class ConnectorMessenger implements Messenger, Terminable {

    private final TownyLink plugin;
    private final TownyRepository repository;
    private final BukkitConnectorPlugin connectorPlugin;
    private final ConnectingPlugin connectingPlugin = () -> "TownyLink";

    @Inject
    public ConnectorMessenger(
        final TownyLink plugin,
        final TownyRepository repository,
        final BukkitConnectorPlugin connectorPlugin
    ) {
        this.plugin = plugin;
        plugin.bind(this);

        this.repository = repository;
        this.connectorPlugin = connectorPlugin;
        Schedulers.bukkit().runTask(plugin, this::registerHandlers); // Will run after "Done!"
    }

    /**
     * Handles incoming messages.
     */
    private void registerHandlers() {

        // --- Update local repository ---

        registerHandler(Action.ADD_TOWN, (player, message) -> {
            String source = message.getSendingServer();
            List<String> townNames = readNames(message.getData());
            this.repository.addAllTownNames(townNames);
            reportReceived(Action.ADD_TOWN, source, townNames);
        });
        registerHandler(Action.ADD_NATION, (player, message) -> {
            String source = message.getSendingServer();
            List<String> nationNames = readNames(message.getData());
            this.repository.addAllNationNames(nationNames);
            reportReceived(Action.ADD_NATION, source, nationNames);
        });
        registerHandler(Action.DELETE_TOWN, (player, message) -> {
            String source = message.getSendingServer();
            List<String> townNames = readNames(message.getData());
            this.repository.removeAllTownName(townNames);
            reportReceived(Action.DELETE_TOWN, source, townNames);
        });
        registerHandler(Action.DELETE_NATION, (player, message) -> {
            String source = message.getSendingServer();
            List<String> nationNames = readNames(message.getData());
            this.repository.removeAllNationNames(nationNames);
            reportReceived(Action.DELETE_NATION, source, nationNames);
        });

        // --- Reply peer requests ---

        // TODO Use callback here (CompletableFuture).
        //  The pattern here is "send request and get response"
        registerHandler(Action.FETCH_TOWN, (player, message) -> { // Reply the requested data
            String source = message.getSendingServer();
            byte[] names = writeNames(TownyUtils.getAllTownNames());
            sendData(Action.ADD_TOWN, MessageTarget.SERVER, source, names);
            reportReceived(Action.FETCH_TOWN, source);
        });
        registerHandler(Action.FETCH_NATION, (player, message) -> { // Reply the requested data
            String source = message.getSendingServer();
            byte[] names = writeNames(TownyUtils.getALlNationNames());
            sendData(Action.ADD_NATION, MessageTarget.SERVER, source, names);
            reportReceived(Action.FETCH_NATION, source);
        });
    }

    @Override public void sendMessage(String action, List<String> data) {
        sendData(action, MessageTarget.OTHERS_QUEUE, writeNames(data));
        reportSent(action, data);
    }

    //// Convenient methods to send/receive data ////

    private void sendData(String action, MessageTarget target, byte[] data) {
        this.connectorPlugin.getConnector().sendData(this.connectingPlugin, action, target, data);
    }

    private void sendData(String action, MessageTarget target, String server, byte[] data) {
        this.connectorPlugin.getConnector().sendData(this.connectingPlugin, action, target, server, data);
    }

    private void registerHandler(String action, BiConsumer<Player, Message> handler) {
        this.connectorPlugin.getConnector().registerMessageHandler(this.connectingPlugin, action, handler);
    }

    //// Methods to read & write byte data ////

    private List<String> readNames(byte[] data) {
        ByteArrayDataInput in = ByteStreams.newDataInput(data);
        int size = in.readInt();
        List<String> names = new ArrayList<>(size);
        for (int i = 0; i < size; i++)
            names.add(in.readUTF());
        return names;
    }

    private byte[] writeNames(List<String> names) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        int size = names.size();
        out.writeInt(size);
        names.forEach(out::writeUTF);
        return out.toByteArray();
    }

    private byte[] writeNames(String... names) {
        return writeNames(Arrays.asList(names));
    }

    //// Methods to report sent & received messages /////

    private void reportReceived(String action, String source, List<String> data) {
        if (data.isEmpty()) {
            reportReceived(action, source);
            return;
        }
        this.plugin.getLogger().info(
            "Recv message | Source: %s | Action: %s | Data: %s".formatted(source, action, data.stream().reduce((a, b) -> a + ", " + b).orElse(""))
        );
    }

    private void reportSent(String action, List<String> data) {
        if (data.isEmpty()) {
            reportSent(action);
            return;
        }
        this.plugin.getLogger().info(
            "Send message | Action: %s | Data: %s".formatted(action, data.stream().reduce((a, b) -> a + ", " + b).orElse(""))
        );
    }

    private void reportReceived(String action, String source) {
        this.plugin.getLogger().info(
            "Recv message | Source: %s | Action: %s".formatted(source, action)
        );
    }

    private void reportSent(String action) {
        this.plugin.getLogger().info(
            "Send message | Action: %s".formatted(action)
        );
    }

    @Override public void close() {
        this.connectorPlugin.getConnector().unregisterMessageHandlers(this.connectingPlugin);
    }

}
