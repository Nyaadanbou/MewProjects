package cc.mewcraft.townyorigin.placeholder;

import cc.mewcraft.mewcore.util.ServerOriginUtils;
import cc.mewcraft.townyorigin.TownyOrigin;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.lucko.helper.terminable.Terminable;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class PAPIPlaceholderExpansion implements Terminable {
    private final TownyOrigin plugin;
    private TownyOriginExpansion expansion;

    @Inject
    public PAPIPlaceholderExpansion(final TownyOrigin plugin) {
        this.plugin = plugin;
    }

    public PAPIPlaceholderExpansion register() {
        if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            return this;
        }

        expansion = new TownyOriginExpansion();
        expansion.register();
        return this;
    }

    @Override public void close() {
        expansion.unregister();
    }

    class TownyOriginExpansion extends PlaceholderExpansion {
        @Override public @Nullable String onRequest(final OfflinePlayer player, final @NotNull String params) {
            if (player == null) {
                return "";
            }

            return switch (params) {
                case "origin_id" -> {
                    String originId = ServerOriginUtils.getOriginId(player.getUniqueId());
                    yield originId != null ? originId : "";
                }
                case "origin_name" -> {
                    String originName = ServerOriginUtils.getOriginName(player.getUniqueId());
                    yield originName != null ? originName : plugin.getLang().of("msg_none_origin").plain();
                }
                default -> "";
            };
        }

        @Override public @NotNull String getIdentifier() {
            return "townyorigin";
        }

        @Override public @NotNull String getAuthor() {
            return "Nailm";
        }

        @Override public @NotNull String getVersion() {
            return "1.0.0";
        }

        @Override public boolean persist() {
            return true;
        }
    }
}
