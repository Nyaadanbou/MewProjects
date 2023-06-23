package cc.mewcraft.townylink.api;

import com.google.common.collect.ImmutableSet;
import me.lucko.helper.promise.Promise;
import me.lucko.helper.terminable.Terminable;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.UUID;

public interface TownyLink extends Terminable {

    /**
     * Requests the town of specific player in specific server.
     */
    @NonNull Promise<TownData> requestPlayerTown(String serverId, UUID playerId);

    /**
     * Requests the nation of specific player in specific server.
     */
    @NonNull Promise<NationData> requestPlayerNation(String serverId, UUID playerId);

    /**
     * Requests all the towns in specific server.
     */
    @NonNull Promise<ImmutableSet<TownData>> requestServerTown(String serverId);

    /**
     * Requests all the nations in specific server.
     */
    @NonNull Promise<ImmutableSet<NationData>> requestServerNation(String serverId);

    /**
     * Requests all the towns across the network.
     * <p>
     * The return promise may be intentionally blocked to allow the messages to be spread.
     */
    @NonNull Promise<ImmutableSet<TownData>> requestGlobalTown();

    /**
     * Requests all the nations across the network.
     * <p>
     * The return promise may be intentionally blocked to allow the messages to be spread.
     */
    @NonNull Promise<ImmutableSet<NationData>> requestGlobalNation();
}
