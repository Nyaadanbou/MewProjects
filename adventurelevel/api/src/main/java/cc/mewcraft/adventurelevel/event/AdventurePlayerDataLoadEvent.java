package cc.mewcraft.adventurelevel.event;

import cc.mewcraft.adventurelevel.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class AdventurePlayerDataLoadEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private final PlayerData playerData;

    public AdventurePlayerDataLoadEvent(final @NotNull Player who, PlayerData playerData) {
        super(who, !Bukkit.isPrimaryThread());
        this.playerData = playerData;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
