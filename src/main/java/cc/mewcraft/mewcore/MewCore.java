package cc.mewcraft.mewcore;

import cc.mewcraft.mewcore.item.api.PluginItemRegistry;
import cc.mewcraft.mewcore.item.impl.*;
import me.lucko.helper.Schedulers;
import me.lucko.helper.plugin.ExtendedJavaPlugin;

public class MewCore extends ExtendedJavaPlugin {

    public static MewCore INSTANCE;

    @Override
    protected void load() {
        registerPluginItems();
    }

    @Override
    protected void enable() {
        INSTANCE = this;
        Schedulers.bukkit().runTask(this, this::postLoad); // run it after "Done!"
    }

    protected void postLoad() {
        // EMPTY
    }

    private void registerPluginItems() {
        PluginItemRegistry.init(this);
        PluginItemRegistry.get().registerForConfig("itemsadder", () -> new ItemsAdderItem(this));
        PluginItemRegistry.get().registerForConfig("mmoitems", () -> new MMOItemsItem(this));
        PluginItemRegistry.get().registerForConfig("brewery", () -> new BreweryItem(this));
        PluginItemRegistry.get().registerForConfig("interactivebooks", () -> new InteractiveBooksItem(this));
        PluginItemRegistry.get().registerForConfig("minecraft", () -> new MinecraftItem(this)); // last fallback
    }

}
