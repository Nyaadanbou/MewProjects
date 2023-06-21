package cc.mewcraft.townylink.impl;

import cc.mewcraft.mewcore.network.ServerInfo;
import cc.mewcraft.townylink.TownyLinkPlugin;
import cc.mewcraft.townylink.api.NationData;
import cc.mewcraft.townylink.api.TownData;
import cc.mewcraft.townylink.api.TownyLink;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import me.lucko.helper.Schedulers;
import me.lucko.helper.messaging.Messenger;
import me.lucko.helper.messaging.conversation.ConversationChannel;
import me.lucko.helper.messaging.conversation.ConversationReply;
import me.lucko.helper.messaging.conversation.ConversationReplyListener;
import me.lucko.helper.promise.Promise;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SimpleTownyLink implements TownyLink {
    private final TownyLinkPlugin plugin;
    private final ConversationChannel<PlayerTownRequest, PlayerTownResponse> playerTownChannel;
    private final ConversationChannel<PlayerNationRequest, PlayerNationResponse> playerNationChannel;
    private final ConversationChannel<ServerTownRequest, ServerTownResponse> serverTownChannel;
    private final ConversationChannel<ServerNationRequest, ServerNationResponse> serverNationChannel;

    public SimpleTownyLink(TownyLinkPlugin plugin, Messenger messenger) {
        this.plugin = plugin;
        this.playerTownChannel = messenger.getConversationChannel("player-town-channel", PlayerTownRequest.class, PlayerTownResponse.class);
        this.playerNationChannel = messenger.getConversationChannel("player-nation-channel", PlayerNationRequest.class, PlayerNationResponse.class);
        this.serverTownChannel = messenger.getConversationChannel("server-town-channel", ServerTownRequest.class, ServerTownResponse.class);
        this.serverNationChannel = messenger.getConversationChannel("server-nation-channel", ServerNationRequest.class, ServerNationResponse.class);
        setupListeners();
    }

    @Override public @NotNull Promise<TownData> requestPlayerTown(final String serverId, final UUID playerId) {
        PlayerTownRequest request = new PlayerTownRequest(serverId, playerId);
        Promise<TownData> promise = Promise.empty();
        playerTownChannel.sendMessage(request, new ConversationReplyListener<>() {
            @Override public @NotNull RegistrationAction onReply(final @NotNull PlayerTownResponse reply) {
                plugin.getSLF4JLogger().info("Request - {}", request);

                promise.supply(reply.townData);
                return ConversationReplyListener.RegistrationAction.STOP_LISTENING;
            }

            @Override public void onTimeout(final @NotNull List<PlayerTownResponse> replies) {
                plugin.getSLF4JLogger().info("Request Timeout - {}", request);

                promise.supplyException(new TimeoutException("Request timed out"));
            }
        }, 1, TimeUnit.SECONDS);
        return promise;
    }

    @Override public @NotNull Promise<List<TownData>> requestServerTown(final String serverId) {
        ServerTownRequest request = new ServerTownRequest(serverId);
        Promise<List<TownData>> promise = Promise.empty();
        serverTownChannel.sendMessage(request, new ConversationReplyListener<>() {
            @Override public @NotNull RegistrationAction onReply(final @NotNull ServerTownResponse reply) {
                plugin.getSLF4JLogger().info("Request - {}", request);

                promise.supply(reply.townDataList);
                return ConversationReplyListener.RegistrationAction.STOP_LISTENING;
            }

            @Override public void onTimeout(final @NotNull List<ServerTownResponse> replies) {
                plugin.getSLF4JLogger().info("Request Timeout - {}", request);

                promise.supplyException(new TimeoutException("Request timed out"));
            }
        }, 1, TimeUnit.SECONDS);
        return promise;
    }

    private void setupListeners() {
        playerTownChannel.newAgent((agent, message) -> {
            String sourceServer = ServerInfo.SERVER_ID.get();
            String serverId = message.serverId;
            if (Objects.equals(serverId, sourceServer)) {
                // This server is the requested server - reply town data

                plugin.getSLF4JLogger().info("Reply - {}", message);

                Promise<PlayerTownResponse> promise = Promise.supplyingAsync(() ->
                    Optional.ofNullable(TownyAPI.getInstance().getResident(message.playerId))
                        .map(Resident::getTownOrNull)
                        .map(town -> {
                            UUID conversationId = message.getConversationId();
                            List<UUID> residents = town.getResidents().stream().map(Resident::getUUID).toList();
                            TownData townData = new TownData(town.getName(), town.getUUID(), town.getSpawnOrNull(), residents);
                            return new PlayerTownResponse(
                                conversationId,
                                sourceServer,
                                townData
                            );
                        }).orElse(null)
                );
                return ConversationReply.ofPromise(promise);
            } else {
                // This server is not the requested server - don't reply it

                plugin.getSLF4JLogger().info("No Reply - {}", message);

                return ConversationReply.noReply();
            }
        });

        playerNationChannel.newAgent((agent, message) -> {
            String sourceServer = ServerInfo.SERVER_ID.get();
            String serverId = message.serverId;
            if (Objects.equals(serverId, sourceServer)) {
                // This server is the requested server - reply town data

                plugin.getSLF4JLogger().info("Reply - {}", message);

                Promise<PlayerNationResponse> promise = Schedulers.async().supply(() ->
                    Optional.ofNullable(TownyAPI.getInstance().getResident(message.playerId))
                        .map(Resident::getNationOrNull)
                        .map(nation -> {
                            UUID conversationId = message.getConversationId();
                            List<UUID> residents = nation.getResidents().stream().map(Resident::getUUID).toList();
                            NationData nationData = new NationData(nation.getName(), nation.getUUID(), nation.getSpawnOrNull(), residents);
                            return new PlayerNationResponse(
                                conversationId,
                                sourceServer,
                                nationData
                            );
                        }).orElse(null)
                );
                return ConversationReply.ofPromise(promise);
            } else {
                // This server is not the requested server - don't reply it

                plugin.getSLF4JLogger().info("No Reply - {}", message);

                return ConversationReply.noReply();
            }
        });
    }
}
