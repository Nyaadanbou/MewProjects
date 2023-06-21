package cc.mewcraft.townylink.api;

import me.lucko.helper.promise.Promise;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public interface TownyLink {

    /**
     * Requests a town response.
     */
    @NotNull Promise<TownData> requestPlayerTown(String serverId, UUID playerId);

    /**
     * Requests all the towns in specific server.
     */
    @NotNull Promise<List<TownData>> requestServerTown(String serverId);
}
