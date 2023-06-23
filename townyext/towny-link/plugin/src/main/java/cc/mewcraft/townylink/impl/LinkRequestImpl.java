package cc.mewcraft.townylink.impl;

import cc.mewcraft.mewcore.network.ServerInfo;
import cc.mewcraft.townylink.TownyLinkPlugin;
import cc.mewcraft.townylink.api.NationData;
import cc.mewcraft.townylink.api.TownData;
import cc.mewcraft.townylink.api.TownyLink;
import cc.mewcraft.townylink.impl.packet.*;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.lucko.helper.messaging.Messenger;
import me.lucko.helper.messaging.conversation.ConversationChannel;
import me.lucko.helper.messaging.conversation.ConversationReplyListener;
import me.lucko.helper.promise.Promise;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Singleton
public class LinkRequestImpl implements TownyLink {
    final TownyLinkPlugin plugin;

    final ConversationChannel<PlayerTownRequest, PlayerTownResponse> playerTownChannel;
    final ConversationChannel<PlayerNationRequest, PlayerNationResponse> playerNationChannel;
    final ConversationChannel<ServerTownRequest, ServerTownResponse> serverTownChannel;
    final ConversationChannel<ServerNationRequest, ServerNationResponse> serverNationChannel;
    final ConversationChannel<GlobalTownRequest, GlobalTownResponse> globalTownChannel;
    final ConversationChannel<GlobalNationRequest, GlobalNationResponse> globalNationChannel;

    @Inject
    public LinkRequestImpl(TownyLinkPlugin plugin) {
        this.plugin = plugin;

        Messenger messenger = plugin.getService(Messenger.class);
        this.playerTownChannel = messenger.getConversationChannel("player-town", PlayerTownRequest.class, PlayerTownResponse.class);
        this.playerNationChannel = messenger.getConversationChannel("player-nation", PlayerNationRequest.class, PlayerNationResponse.class);
        this.serverTownChannel = messenger.getConversationChannel("server-town", ServerTownRequest.class, ServerTownResponse.class);
        this.serverNationChannel = messenger.getConversationChannel("server-nation", ServerNationRequest.class, ServerNationResponse.class);
        this.globalTownChannel = messenger.getConversationChannel("global-town", GlobalTownRequest.class, GlobalTownResponse.class);
        this.globalNationChannel = messenger.getConversationChannel("global-nation", GlobalNationRequest.class, GlobalNationResponse.class);

        if (plugin.isPluginPresent("Towny")) {
            // To prevent CNF when Towny is not installed on this server
            new LinkResponseImpl(this).setupResponseHandlers();
        }
    }

    @Override public void close() {
        playerTownChannel.close();
        playerNationChannel.close();
        serverTownChannel.close();
        serverNationChannel.close();
        globalTownChannel.close();
        globalNationChannel.close();
    }

    @Override public @NonNull Promise<TownData> requestPlayerTown(final String serverId, final UUID playerId) {
        PlayerTownRequest request = new PlayerTownRequest(serverId, playerId);
        Promise<TownData> promise = Promise.empty();
        plugin.getSLF4JLogger().debug("Request - {}", request);

        // Send request and await replies
        playerTownChannel.sendMessage(request, new ConversationReplyListener<>() {
            @Override public @NonNull RegistrationAction onReply(final @NonNull PlayerTownResponse reply) {
                promise.supply(reply.townData);
                return ConversationReplyListener.RegistrationAction.STOP_LISTENING;
            }

            @Override public void onTimeout(final @NonNull List<PlayerTownResponse> replies) {
                plugin.getSLF4JLogger().debug("Request Timeout - {}", request);
                promise.supplyException(new TimeoutException("Request timed out"));
            }
        }, 1, TimeUnit.SECONDS);

        return promise;
    }

    @Override public @NonNull Promise<NationData> requestPlayerNation(final String serverId, final UUID playerId) {
        PlayerNationRequest request = new PlayerNationRequest(serverId, playerId);
        Promise<NationData> promise = Promise.empty();
        plugin.getSLF4JLogger().debug("Request - {}", request);

        // Send request and await replies
        playerNationChannel.sendMessage(request, new ConversationReplyListener<>() {
            @Override public @NonNull RegistrationAction onReply(final @NonNull PlayerNationResponse reply) {
                promise.supply(reply.nationData);
                return ConversationReplyListener.RegistrationAction.STOP_LISTENING;
            }

            @Override public void onTimeout(final @NonNull List<PlayerNationResponse> replies) {
                plugin.getSLF4JLogger().debug("Request Timeout - {}", request);
                promise.supplyException(new TimeoutException("Request timed out"));
            }
        }, 1, TimeUnit.SECONDS);

        return promise;
    }

    @Override public @NonNull Promise<ImmutableSet<TownData>> requestServerTown(final String serverId) {
        ServerTownRequest request = new ServerTownRequest(serverId);
        Promise<ImmutableSet<TownData>> promise = Promise.empty();
        plugin.getSLF4JLogger().debug("Request - {}", request);

        // Send request and await replies
        serverTownChannel.sendMessage(request, new ConversationReplyListener<>() {
            @Override public @NonNull RegistrationAction onReply(final @NonNull ServerTownResponse reply) {
                promise.supply(reply.townDataSet);
                return ConversationReplyListener.RegistrationAction.STOP_LISTENING;
            }

            @Override public void onTimeout(final @NonNull List<ServerTownResponse> replies) {
                plugin.getSLF4JLogger().debug("Request Timeout - {}", request);
                promise.supplyException(new TimeoutException("Request timed out"));
            }
        }, 1, TimeUnit.SECONDS);

        return promise;
    }

    @Override public @NonNull Promise<ImmutableSet<NationData>> requestServerNation(final String serverId) {
        ServerNationRequest request = new ServerNationRequest(serverId);
        Promise<ImmutableSet<NationData>> promise = Promise.empty();
        plugin.getSLF4JLogger().debug("Request - {}", request);

        // Send request and await replies
        serverNationChannel.sendMessage(request, new ConversationReplyListener<>() {
            @Override public @NonNull RegistrationAction onReply(final @NonNull ServerNationResponse reply) {
                promise.supply(reply.nationDataSet);
                return ConversationReplyListener.RegistrationAction.STOP_LISTENING;
            }

            @Override public void onTimeout(final @NonNull List<ServerNationResponse> replies) {
                plugin.getSLF4JLogger().debug("Request Timeout - {}", request);
                promise.supplyException(new TimeoutException("Request timed out"));
            }
        }, 1, TimeUnit.SECONDS);

        return promise;
    }

    @Override public @NonNull Promise<ImmutableSet<TownData>> requestGlobalTown() {
        String senderServer = ServerInfo.SERVER_ID.get();
        GlobalTownRequest request = new GlobalTownRequest(senderServer);
        Promise<ImmutableSet<TownData>> promise = Promise.empty();
        plugin.getSLF4JLogger().debug("Request - {}", request);

        // Send request and await replies
        globalTownChannel.sendMessage(request, new ConversationReplyListener<>() {
            @Override public @NotNull RegistrationAction onReply(final @NotNull GlobalTownResponse reply) {
                return RegistrationAction.CONTINUE_LISTENING;
            }

            @Override public void onTimeout(final @NotNull List<GlobalTownResponse> replies) {
                // The method onTimeout() is a straight way to collect
                // all the replies within the duration of request timeout

                plugin.getSLF4JLogger().debug("Request Done - {}", request);
                promise.supply(replies
                    .stream()
                    .flatMap(t -> t.townDataSet.stream())
                    .collect(ImmutableSet.toImmutableSet())
                );
            }
        }, 1, TimeUnit.SECONDS /* spend 1s receiving responses from ALL servers */);

        return promise;
    }

    @Override public @NonNull Promise<ImmutableSet<NationData>> requestGlobalNation() {
        String senderServer = ServerInfo.SERVER_ID.get();
        GlobalNationRequest request = new GlobalNationRequest(senderServer);
        Promise<ImmutableSet<NationData>> promise = Promise.empty();
        plugin.getSLF4JLogger().debug("Request - {}", request);

        // Send request and await replies
        globalNationChannel.sendMessage(request, new ConversationReplyListener<>() {
            @Override public @NotNull RegistrationAction onReply(final @NotNull GlobalNationResponse reply) {
                return RegistrationAction.CONTINUE_LISTENING;
            }

            @Override public void onTimeout(final @NotNull List<GlobalNationResponse> replies) {
                plugin.getSLF4JLogger().debug("Request Done - {}", request);
                promise.supply(replies
                    .stream()
                    .flatMap(t -> t.nationDataSet.stream())
                    .collect(ImmutableSet.toImmutableSet())
                );
            }
        }, 1, TimeUnit.SECONDS /* spend 1s receiving responses from ALL servers */);

        return promise;
    }
}
