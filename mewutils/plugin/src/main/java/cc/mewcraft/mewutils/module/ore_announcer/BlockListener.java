package cc.mewcraft.mewutils.module.ore_announcer;

import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockListener implements AutoCloseableListener {

    private final OreAnnouncerModule module;

    public BlockListener(final OreAnnouncerModule module) {
        this.module = module;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (!this.module.shouldAnnounce(event.getBlock()))
            return;

        Component prefix = this.module.getLang().of("found_ores.prefix").component();
        Component message = prefix.append(this.module.getLang().of("found_ores.found")
            .resolver(Placeholder.component("player", event.getPlayer().displayName()))
            .resolver(Placeholder.component("ore", Component.text(event.getBlock().getType().translationKey())))
            .replace("count", this.module.getBlockCounter().count(event.getBlock().getLocation(), event.getBlock().getType()))
            .component());
        Bukkit.getServer()
            .filterAudience(audience -> this.module.isSubscriber(event.getPlayer().getUniqueId()))
            .sendMessage(message);
    }

}
