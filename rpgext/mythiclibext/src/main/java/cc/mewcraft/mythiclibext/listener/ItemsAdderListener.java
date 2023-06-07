package cc.mewcraft.mythiclibext.listener;

import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import cc.mewcraft.mythiclibext.MythicLibExt;
import cc.mewcraft.mythiclibext.object.ItemsAdderStatus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

@Singleton
public class ItemsAdderListener implements AutoCloseableListener {

    private final @NotNull MythicLibExt plugin;

    @Inject
    public ItemsAdderListener(final @NotNull MythicLibExt plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemsAdderLoad(ItemsAdderLoadDataEvent event) {
        ItemsAdderStatus.markAsComplete();

        // In 98% of times of reloading ItemsAdder,
        // we don't add/remove items from ItemsAdder.

        // So we only auto reload MMOItems for the first event.
        // (Usually the first event is fired at server startup)

        if (ItemsAdderStatus.completeCount() == 1) {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mmoitems reload all");
        }
    }

}
