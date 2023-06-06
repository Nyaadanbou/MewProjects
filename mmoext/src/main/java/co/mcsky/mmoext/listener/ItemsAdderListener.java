package co.mcsky.mmoext.listener;

import co.mcsky.mmoext.RPGBridge;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import me.lucko.helper.terminable.Terminable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class ItemsAdderListener implements Listener, Terminable {

    public ItemsAdderListener() {
        RPGBridge.inst().registerListener(this);
    }

    @EventHandler
    public void onItemsAdderLoad(ItemsAdderLoadDataEvent event) {
        // mark itemsadder has fully loaded
        // this flag may be used by some code
        RPGBridge.ITEMSADDER_LOADED = true;

        // 90% of times we actually don't add/remove items from items adder
        // so the auto reloading of other plugins is just a redundant idea

        // Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mmoitems reload stations");
        // Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mmoitems reload recipes");
        RPGBridge.config().loadSummonItems();
    }

    @Override
    public void close() {
        HandlerList.unregisterAll(this);
    }

}
