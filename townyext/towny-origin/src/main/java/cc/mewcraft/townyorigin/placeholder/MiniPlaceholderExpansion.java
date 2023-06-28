package cc.mewcraft.townyorigin.placeholder;

import cc.mewcraft.mewcore.util.ServerOriginUtils;
import cc.mewcraft.townyorigin.TownyOrigin;
import io.github.miniplaceholders.api.Expansion;
import me.lucko.helper.terminable.Terminable;
import net.kyori.adventure.text.minimessage.tag.Tag;
import org.bukkit.entity.Player;

public class MiniPlaceholderExpansion implements Terminable {

    private final TownyOrigin plugin;
    private Expansion expansion;

    public MiniPlaceholderExpansion(final TownyOrigin plugin) {
        this.plugin = plugin;
    }

    public MiniPlaceholderExpansion register() {
        if (plugin.getServer().getPluginManager().getPlugin("MiniPlaceholders") == null) {
            return this;
        }

        this.expansion = Expansion.builder("townyorigin")
            .filter(Player.class)

            // return server-origin-id
            .audiencePlaceholder("origin_id", (audience, queue, ctx) -> {
                Player player = (Player) audience;
                String serverOriginId = ServerOriginUtils.getOriginId(player.getUniqueId());
                return Tag.preProcessParsed(serverOriginId != null ? serverOriginId : "");
            })

            // return server-origin-name
            .audiencePlaceholder("origin_name", (audience, queue, ctx) -> {
                Player player = (Player) audience;
                String serverOriginName = ServerOriginUtils.getOriginName(player.getUniqueId());
                return Tag.preProcessParsed(serverOriginName != null
                    ? serverOriginName
                    : plugin.getLang().of("msg_none_origin").plain()
                );
            })

            .build();

        this.expansion.register();

        return this;
    }

    @Override public void close() throws Exception {
        expansion.unregister();
    }

}
