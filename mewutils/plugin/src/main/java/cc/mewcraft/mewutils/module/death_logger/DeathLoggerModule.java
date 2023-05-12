package cc.mewcraft.mewutils.module.death_logger;

import cc.mewcraft.mewutils.api.MewPlugin;
import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import cc.mewcraft.mewutils.api.module.ModuleBase;
import com.google.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class DeathLoggerModule extends ModuleBase implements AutoCloseableListener {

    private Set<EntityType> logTypes;
    private int searchRadius;

    @Inject
    public DeathLoggerModule(MewPlugin plugin) {
        super(plugin);
    }

    @Override protected void load() throws Exception {
        this.logTypes = getConfigNode().node("entities")
            .getList(String.class, List.of())
            .stream()
            .map(s -> EntityType.valueOf(s.toUpperCase(Locale.ROOT)))
            .collect(Collectors.toSet());
        this.searchRadius = getConfigNode().node("search_radius").getInt();
    }

    @Override protected void enable() {
        registerListener(this);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if (!this.logTypes.contains(event.getEntityType()))
            return;

        if (entity.getLastDamageCause() == null)
            return;

        getLang().of("death")
            .replace("victim", Optional.ofNullable(entity.customName()).orElse(entity.name()))
            .replace("reason", getLocalization(entity.getLastDamageCause().getCause()))
            .replace("killer", Optional.ofNullable(entity.getKiller()).map(Player::getName).orElseGet(() -> entity
                .getLocation()
                .getNearbyPlayers(this.searchRadius)
                .stream()
                .map(Player::getName)
                .reduce((acc, name) -> acc.concat(",").concat(name))
                .orElse(getParentPlugin().getLang().of("none").plain())
            ))
            .replace("x", entity.getLocation().getBlockX())
            .replace("y", entity.getLocation().getBlockY())
            .replace("z", entity.getLocation().getBlockZ())
            .replace("world", entity.getLocation().getWorld().getName())
            .send(Bukkit.getServer()); // broadcast to entire server
    }

    private String getLocalization(EntityDamageEvent.DamageCause cause) {
        return switch (cause) {
            case FALL -> "跌落";
            case FIRE, FIRE_TICK -> "火烧";
            case LAVA -> "岩浆";
            case VOID -> "虚空";
            case MAGIC -> "魔法";
            case CUSTOM -> "自定义";
            case DRYOUT -> "干渴";
            case FREEZE -> "冰冻";
            case POISON -> "中毒";
            case THORNS -> "荆棘";
            case WITHER -> "凋零";
            case CONTACT -> "接触";
            case MELTING -> "融化";
            case SUICIDE -> "自杀";
            case CRAMMING -> "拥挤";
            case DROWNING -> "淹死";
            case HOT_FLOOR -> "高温地板";
            case LIGHTNING -> "闪电";
            case ENTITY_ATTACK -> "生物攻击";
            case ENTITY_SWEEP_ATTACK -> "生物横扫攻击";
            case PROJECTILE -> "弹射物";
            case STARVATION -> "饥饿";
            case SUFFOCATION -> "窒息";
            case BLOCK_EXPLOSION -> "方块爆炸";
            case ENTITY_EXPLOSION -> "实体爆炸";
            case FALLING_BLOCK -> "跌落方块";
            case DRAGON_BREATH -> "龙息";
            case FLY_INTO_WALL -> "卡进墙里";
            case SONIC_BOOM -> "音爆";
        };
    }

    @Override public boolean checkRequirement() {
        return true;
    }

}
