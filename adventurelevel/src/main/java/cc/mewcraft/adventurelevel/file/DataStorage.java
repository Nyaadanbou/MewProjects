package cc.mewcraft.adventurelevel.file;

import cc.mewcraft.adventurelevel.data.PlayerData;
import me.lucko.helper.promise.Promise;
import me.lucko.helper.terminable.Terminable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface DataStorage extends Terminable {

    /**
     * @param uuid the uuid of PlayerData
     *
     * @return a PlayerData wrapped by Promise
     */
    @NotNull Promise<PlayerData> create(UUID uuid);

    /**
     * @param uuid the uuid of PlayerData
     *
     * @return a PlayerData wrapped by Promise
     */
    @NotNull Promise<PlayerData> load(UUID uuid);

    /**
     * Saves the given PlayerData asynchronously.
     *
     * @param playerData the playerData to save
     */
    void save(PlayerData playerData);

    /**
     * Initialise this data storage.
     */
    void init();

}
