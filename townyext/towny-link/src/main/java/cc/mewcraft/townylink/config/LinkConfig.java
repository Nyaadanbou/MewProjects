package cc.mewcraft.townylink.config;

import cc.mewcraft.townylink.TownyLinkPlugin;
import cc.mewcraft.townylink.impl.PlayerNationRequest;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;

@Singleton
public class LinkConfig {

    private final TownyLinkPlugin plugin;
    private final List<String> targetServers; // currently not used

    @Inject
    public LinkConfig(final TownyLinkPlugin plugin) {
        this.plugin = plugin;
        this.targetServers = plugin.getConfig().getStringList("target-servers");
    }

    public List<String> getTargetServers() {
        return this.targetServers;
    }

}
