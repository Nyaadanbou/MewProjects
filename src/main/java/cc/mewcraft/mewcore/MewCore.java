package cc.mewcraft.mewcore;

import cc.mewcraft.mewcore.item.api.PluginItemRegistry;
import cc.mewcraft.mewcore.item.impl.*;
import me.lucko.helper.Schedulers;
import me.lucko.helper.plugin.ExtendedJavaPlugin;

import java.util.logging.Logger;

public class MewCore extends ExtendedJavaPlugin {

    public static MewCore INSTANCE;

    public static Logger logger() {
        return INSTANCE.getLogger();
    }

    @Override
    protected void disable() {}

    @Override
    protected void enable() {
        INSTANCE = this;

        PluginItemRegistry.init(this);
        PluginItemRegistry.get().registerForConfig("itemsadder", () -> new ItemsAdderItem(this));
        PluginItemRegistry.get().registerForConfig("mmoitems", () -> new MMOItemsItem(this));
        PluginItemRegistry.get().registerForConfig("brewery", () -> new BreweryItem(this));
        PluginItemRegistry.get().registerForConfig("interactivebooks", () -> new InteractiveBooksItem(this));
        PluginItemRegistry.get().registerForConfig("minecraft", () -> new MinecraftItem(this)); // last fallback

        Schedulers.sync().run(this::postEnable).bindWith(this);
    }

    protected void postEnable() {
        // EMPTY
    }

}
