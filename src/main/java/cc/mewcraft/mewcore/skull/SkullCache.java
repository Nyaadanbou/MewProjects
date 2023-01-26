package cc.mewcraft.mewcore.skull;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.lucko.helper.Events;
import me.lucko.helper.cooldown.Cooldown;
import me.lucko.helper.promise.Promise;
import me.lucko.helper.terminable.Terminable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Head cache pool for non-blocking player head display.
 */
public enum SkullCache implements Terminable {

    // singleton
    INSTANCE;

    // the skull cache
    private final Cache<UUID, ItemStack> cache;

    // all scheduled (async) tasks
    private final Set<Terminable> scheduledTasks;

    // uuids which are already scheduled tasks to fetch skin
    private final Set<UUID> fetching;

    // cooldown to fire the fetch complete event
    private final Cooldown eventThreshold;

    SkullCache() {
        this.cache = CacheBuilder.newBuilder().expireAfterWrite(6, TimeUnit.HOURS).build();
        this.fetching = new HashSet<>();
        this.scheduledTasks = new HashSet<>();
        this.eventThreshold = Cooldown.of(200, TimeUnit.MILLISECONDS);
    }

    /**
     * Clears the cache and terminates all scheduled tasks.
     */
    public void clear() {
        scheduledTasks.forEach(Terminable::closeSilently);
        scheduledTasks.clear();
        fetching.clear();
        cache.invalidateAll();
    }

    /**
     * Modifies the head to give it a texture of the given player's UUID. The
     * {@code item} will leave unchanged if the texture has not been fetched yet
     * when calling this method.
     *
     * @param uuid the UUID of the player
     * @param item the head to be modified
     */
    public void itemWithUuid(ItemStack item, UUID uuid) {
        // Gets a copy of item meta of the item
        ItemMeta itemMeta = item.getItemMeta();

        if (!(itemMeta instanceof SkullMeta)) {
            // item is not a skull
            return;
        }

        if (cache.getIfPresent(uuid) != null) {
            // if cached, updates the skull's texture

            mutateProfile(item, uuid);
        } else {
            // schedules a task to fetch the skull texture

            fetch(item, uuid);
        }
    }

    /**
     * Schedules a task to fetch the skull texture of given player's UUID.
     * <p>
     * The texture to be fetched will be put into the cache ONLY when the task
     * completes so that subsequent "get" calls on the cache will return the
     * fetched textures.
     *
     * @param uuid the UUID of the skull texture
     */
    private void fetch(ItemStack item, UUID uuid) {
        if (!fetching.contains(uuid)) {
            // schedule the fetch task iff the uuid is not being fetched right now

            // mark this uuid is being fetched
            fetching.add(uuid);

            Promise<Void> fetchTask = Promise.start()
                    .thenApplyAsync(n -> SkullCreator.itemWithUuid(item, uuid))
                    .thenAcceptSync(fetched -> {
                        cache.put(uuid, fetched);
                        mutateProfile(item, uuid);

                        // fetched, un-mark the uuid
                        fetching.remove(uuid);

                        // call event so that GUIs containing skull items
                        // may refresh the skull textures as being fetched
                        if (fetching.isEmpty() || eventThreshold.test()) {
                            Events.call(new SkinFetchCompleteEvent());
                        }
                    });

            scheduledTasks.add(fetchTask);
        }
    }

    /**
     * Modifies the head to have the texture obtained from the cache.
     *
     * @param origin the head to be modified
     * @param uuid   the player's UUID
     */
    private void mutateProfile(ItemStack origin, UUID uuid) {
        final ItemStack nullable = cache.getIfPresent(uuid);
        Preconditions.checkNotNull(nullable); // this should never be null otherwise it's a fatal error

        SkullMeta originSkullMeta = (SkullMeta) origin.getItemMeta();
        SkullMeta cachedSkullMeta = (SkullMeta) nullable.getItemMeta();

        // only set the player profile to avoid unexpected duplicated lore
        originSkullMeta.setPlayerProfile(cachedSkullMeta.getPlayerProfile());
        origin.setItemMeta(originSkullMeta);
    }

    @Override
    public void close() {
        clear();
    }
}
