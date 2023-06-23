package cc.mewcraft.townylink.impl;

import cc.mewcraft.mewcore.network.ServerInfo;
import cc.mewcraft.townylink.api.NationData;
import cc.mewcraft.townylink.api.TownData;
import cc.mewcraft.townylink.impl.packet.*;
import com.google.common.collect.ImmutableSet;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.lucko.helper.messaging.conversation.ConversationReply;
import me.lucko.helper.promise.Promise;
import me.lucko.helper.serialize.Position;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;
import java.util.UUID;

/**
 * There are two purposes of this class:
 * <ol>
 *     <li>It's used to decouple the implementation of {@link cc.mewcraft.townylink.api.TownyLink}, and therefore</li>
 *     <li>It allows the instance of {@link cc.mewcraft.townylink.api.TownyLink} to be constructed when runtime has no Towny</li>
 * </ol>
 * Therefore, this class should only be constructed if Towny is present at runtime.
 * Otherwise, CNF will occur.
 */
class LinkResponseImpl { // package private
    private final LinkRequestImpl linkRequest;

    LinkResponseImpl(LinkRequestImpl linkRequest) {
        this.linkRequest = linkRequest;
    }

    void setupResponseHandlers() {
        linkRequest.playerTownChannel.newAgent((agent, message) -> {
            String sourceServer = ServerInfo.SERVER_ID.get();
            String serverId = message.serverId;
            if (Objects.equals(serverId, sourceServer)) {
                // This server is the requested server - reply town data

                Promise<PlayerTownResponse> promise = Promise.supplyingAsync(() -> {
                    Resident resident = TownyAPI.getInstance().getResident(message.playerId);
                    if (resident == null) {
                        linkRequest.plugin.getSLF4JLogger().warn("Null Reply - No resident found - {}", message);
                        return null;
                    }

                    Town town = resident.getTownOrNull();
                    if (town == null) {
                        linkRequest.plugin.getSLF4JLogger().warn("Null Reply - No town found - {}", message);
                        return null;
                    }

                    try {
                        UUID conversationId = message.getConversationId();
                        ImmutableSet<UUID> residents = town.getResidents().stream().map(Resident::getUUID).collect(ImmutableSet.toImmutableSet());
                        TownData townData = new TownData(town.getName(), town.getUUID(), Position.of(town.getSpawn()), residents);
                        linkRequest.plugin.getSLF4JLogger().info("Reply - {}", message);
                        return new PlayerTownResponse(conversationId, sourceServer, townData);
                    } catch (TownyException e) {
                        linkRequest.plugin.getSLF4JLogger().warn("Null Reply - No spawn location found - {}", message);
                        return null;
                    }
                });
                return ConversationReply.ofPromise(promise);
            } else {
                // This server is not the requested server - don't reply it

                linkRequest.plugin.getSLF4JLogger().info("No Reply - {}", message);
                return ConversationReply.noReply();
            }
        });

        linkRequest.playerNationChannel.newAgent((agent, message) -> {
            String sourceServer = ServerInfo.SERVER_ID.get();
            String serverId = message.serverId;
            if (Objects.equals(serverId, sourceServer)) {
                // This server is the requested server - reply town data

                Promise<PlayerNationResponse> promise = Promise.supplyingAsync(() -> {
                    Resident resident = TownyAPI.getInstance().getResident(message.playerId);
                    if (resident == null) {
                        linkRequest.plugin.getSLF4JLogger().warn("Null Reply - No resident found - {}", message);
                        return null;
                    }

                    Nation nation = resident.getNationOrNull();
                    if (nation == null) {
                        linkRequest.plugin.getSLF4JLogger().warn("Null Reply - No nation found - {}", message);
                        return null;
                    }

                    try {
                        UUID conversationId = message.getConversationId();
                        ImmutableSet<UUID> residents = nation.getResidents().stream().map(Resident::getUUID).collect(ImmutableSet.toImmutableSet());
                        NationData nationData = new NationData(nation.getName(), nation.getUUID(), Position.of(nation.getSpawn()), residents);
                        linkRequest.plugin.getSLF4JLogger().info("Reply - {}", message);
                        return new PlayerNationResponse(conversationId, sourceServer, nationData);
                    } catch (TownyException e) {
                        linkRequest.plugin.getSLF4JLogger().warn("Null Reply - No spawn location found - {}", message);
                        return null;
                    }
                });
                return ConversationReply.ofPromise(promise);
            } else {
                // This server is not the requested server - don't reply it

                linkRequest.plugin.getSLF4JLogger().info("No Reply - {}", message);
                return ConversationReply.noReply();
            }
        });

        linkRequest.serverTownChannel.newAgent((agent, message) -> {
            String sourceServer = ServerInfo.SERVER_ID.get();
            String serverId = message.serverId;
            if (Objects.equals(serverId, sourceServer)) {
                // This server is the requested server - reply town data list

                Promise<ServerTownResponse> promise = Promise.supplyingAsync(() -> {
                    ImmutableSet<TownData> townDataList = getTowns();
                    linkRequest.plugin.getSLF4JLogger().info("Reply - {}", message);
                    return new ServerTownResponse(message.getConversationId(), sourceServer, townDataList);
                });
                return ConversationReply.ofPromise(promise);
            } else {
                // This server is not the requested server - don't reply it

                linkRequest.plugin.getSLF4JLogger().info("No Reply - {}", message);
                return ConversationReply.noReply();
            }
        });

        linkRequest.serverNationChannel.newAgent((agent, message) -> {
            String sourceServer = ServerInfo.SERVER_ID.get();
            String serverId = message.serverId;
            if (Objects.equals(serverId, sourceServer)) {
                // This server is the requested server - reply nation data list

                Promise<ServerNationResponse> promise = Promise.supplyingAsync(() -> {
                    ImmutableSet<NationData> nationDataList = getNations();
                    linkRequest.plugin.getSLF4JLogger().info("Reply - {}", message);
                    return new ServerNationResponse(message.getConversationId(), sourceServer, nationDataList);
                });
                return ConversationReply.ofPromise(promise);
            } else {
                // This server is not the requested server - don't reply it

                linkRequest.plugin.getSLF4JLogger().info("No Reply - {}", message);
                return ConversationReply.noReply();
            }
        });

        linkRequest.globalTownChannel.newAgent((agent, message) -> {
            String sourceServer = ServerInfo.SERVER_ID.get();
            String senderServer = message.senderServer;
            if (!Objects.equals(sourceServer, senderServer)) {
                // This server is not the sender server - reply town data list

                Promise<GlobalTownResponse> promise = Promise.supplyingAsync(() -> {
                    ImmutableSet<TownData> townDataList = getTowns();
                    linkRequest.plugin.getSLF4JLogger().info("Reply - {}", message);
                    return new GlobalTownResponse(message.getConversationId(), townDataList);
                });
                return ConversationReply.ofPromise(promise);
            } else {
                // This server is the sender itself

                linkRequest.plugin.getSLF4JLogger().info("No Reply - {}", message);
                return ConversationReply.noReply();
            }
        });

        linkRequest.globalNationChannel.newAgent((agent, message) -> {
            String sourceServer = ServerInfo.SERVER_ID.get();
            String senderServer = message.senderServer;
            if (!Objects.equals(sourceServer, senderServer)) {
                // This server is not the sender server - reply nation data list

                Promise<GlobalNationResponse> promise = Promise.supplyingAsync(() -> {
                    ImmutableSet<NationData> nationDataList = getNations();
                    linkRequest.plugin.getSLF4JLogger().info("Reply - {}", message);
                    return new GlobalNationResponse(message.getConversationId(), nationDataList);
                });
                return ConversationReply.ofPromise(promise);
            } else {
                // This server is the sender itself

                linkRequest.plugin.getSLF4JLogger().info("No Reply - {}", message);
                return ConversationReply.noReply();
            }
        });
    }

    /**
     * Gets all the towns in this server.
     */
    private @NonNull ImmutableSet<TownData> getTowns() {
        ImmutableSet.Builder<TownData> townDataSet = ImmutableSet.builder();
        for (final Town town : TownyAPI.getInstance().getTowns()) {
            try {
                townDataSet.add(new TownData(
                    town.getName(),
                    town.getUUID(),
                    Position.of(town.getSpawn()),
                    town.getResidents().stream().map(Resident::getUUID).collect(ImmutableSet.toImmutableSet())
                ));
            } catch (TownyException e) {
                // Skip towns without spawn location
                linkRequest.plugin.getSLF4JLogger().warn("No spawn location found for town \"{}\"", town.getName());
            }

        }
        return townDataSet.build();
    }

    /**
     * Gets all the nations in this server.
     */
    private @NonNull ImmutableSet<NationData> getNations() {
        ImmutableSet.Builder<NationData> nationDataSet = ImmutableSet.builder();
        for (final Nation nation : TownyAPI.getInstance().getNations()) {
            try {
                nationDataSet.add(new NationData(
                    nation.getName(),
                    nation.getUUID(),
                    Position.of(nation.getSpawn()),
                    nation.getResidents().stream().map(Resident::getUUID).collect(ImmutableSet.toImmutableSet())
                ));
            } catch (TownyException e) {
                // Skip nations without spawn location
                linkRequest.plugin.getSLF4JLogger().warn("No spawn location found for town \"{}\"", nation.getName());
            }
        }
        return nationDataSet.build();
    }
}