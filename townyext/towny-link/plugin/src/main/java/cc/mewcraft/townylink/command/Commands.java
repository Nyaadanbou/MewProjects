package cc.mewcraft.townylink.command;

import cc.mewcraft.townylink.TownyLinkPlugin;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class Commands {
    private final TownyLinkPlugin plugin;

    @Inject
    public Commands(TownyLinkPlugin plugin) {
        this.plugin = plugin;
    }
}
