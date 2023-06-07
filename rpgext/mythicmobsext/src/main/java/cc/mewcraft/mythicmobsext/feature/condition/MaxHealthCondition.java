package cc.mewcraft.mythicmobsext.feature.condition;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;

@MythicCondition(
    author = "Nailm",
    name = "maxhealth",
    aliases = {},
    description = "Tests if any of nearby players reaches specific maximum health"
)
public class MaxHealthCondition implements ILocationCondition {
    private final double maxHealth;
    private final double searchRadius;

    public MaxHealthCondition(MythicLineConfig config) {
        maxHealth = config.getDouble("amount", 20);
        searchRadius = config.getDouble("radius", 32);
    }

    @Override public boolean check(final AbstractLocation abstractLocation) {
        Location location = BukkitAdapter.adapt(abstractLocation);

        //noinspection DataFlowIssue
        return location.getNearbyPlayers(searchRadius)
            .stream().findAny()
            .map(player -> player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() >= maxHealth)
            .orElse(false);
    }
}
