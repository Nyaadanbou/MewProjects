package cc.mewcraft.townyorigin.placeholder;

import cc.mewcraft.mewcore.util.ServerOriginUtils;
import cc.mewcraft.townyorigin.TownyOrigin;
import io.github.miniplaceholders.api.Expansion;
import me.lucko.helper.terminable.Terminable;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.entity.Player;

public class MiniPlaceholderExpansion implements Terminable {

    private final TownyOrigin plugin;
    private final LuckPerms luckPerms;
    private Expansion expansion;

    public MiniPlaceholderExpansion(final TownyOrigin plugin) {
        this.plugin = plugin;
        this.luckPerms = LuckPermsProvider.get();
    }

    public MiniPlaceholderExpansion register() {
        this.expansion = Expansion.builder("townyorigin")
            .filter(Player.class)

            // return server-origin-id
            .audiencePlaceholder("origin_id", (audience, queue, ctx) -> {
                Player player = (Player) audience;
                String serverOriginId = ServerOriginUtils.getOrigin(player.getUniqueId());
                return Tag.preProcessParsed(serverOriginId != null ? serverOriginId : "");
            })

            // return server-origin-name
            .audiencePlaceholder("origin_name", (audience, queue, ctx) -> {
                Player player = (Player) audience;
                String serverOriginName = ServerOriginUtils.getOrigin(player.getUniqueId());
                return Tag.preProcessParsed(serverOriginName != null
                    ? serverOriginName
                    : plugin.getLang().of("ph_none_origin").plain()
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
