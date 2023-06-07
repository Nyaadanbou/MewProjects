package cc.mewcraft.mythicmobsext.feature.option.display;

import cc.mewcraft.mythicmobsext.MythicMobsExt;
import cc.mewcraft.mythicmobsext.feature.option.PlayerAttackHandler;
import cc.mewcraft.mythicmobsext.feature.option.crit.CriticalUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.damage.DamagePacket;
import io.lumine.mythic.lib.damage.DamageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * This handler shows damage information to the player who attacks certain MM mobs.
 */
@Singleton
public class DamageDisplayHandler implements PlayerAttackHandler {

    private final @NotNull MythicMobsExt plugin;
    private final @NotNull DamageDisplayManager damageDisplayManager;

    @Inject
    public DamageDisplayHandler(
        final @NotNull MythicMobsExt plugin,
        final @NotNull DamageDisplayManager damageDisplayManager
    ) {
        this.plugin = plugin;
        this.damageDisplayManager = damageDisplayManager;
    }

    @Override
    public void handle(@NotNull PlayerAttackEvent event, @NotNull ActiveMob activeMob) {
        DamageDisplayParams params = damageDisplayManager.getParameters(activeMob.getType());
        if (params == null) return; // If the MM mob isn't being tracked

        DamageMetadata damageMeta = event.getDamage();
        Player player = event.getAttacker().getPlayer();

        @SuppressWarnings("deprecation")
        String victimName = ChatColor.stripColor(activeMob.getName()); // must strip it since MythicMobs return string containing section symbols

        Component headText = plugin.getLang().of("damage.head_text").replace("victim", victimName).locale(player).component();
        Component tailText = plugin.getLang().of("damage.tail_text").locale(player).component();
        Component packetSeparator = plugin.getLang().of("damage.packet_separator").locale(player).component();
        Component typeSeparator = plugin.getLang().of("damage.type_separator").locale(player).component();

        TextComponent.Builder allPacketText = Component.text();

        boolean packetAppended = false;
        for (DamagePacket packet : damageMeta.getPackets()) {
            if (packetAppended) {
                allPacketText.append(packetSeparator);
            }
            TextComponent.Builder type = Component.text();
            boolean typeAppended = false;
            for (DamageType requiredType : params.damageTypes()) {
                if (containsType(packet.getTypes(), requiredType)) {
                    if (typeAppended) {
                        type.append(typeSeparator);
                    }
                    type.append(plugin.getLang().of("damage.type." + requiredType.name().toLowerCase()).locale(player).component());
                    typeAppended = true;
                }
            }
            if (typeAppended) { // If this DamagePacket contains the DamageType we want
                Component value;
                if (CriticalUtils.isVanillaCriticalHit(event) || damageMeta.isWeaponCriticalStrike() || damageMeta.isSkillCriticalStrike()) {
                    value = Component.text(plugin.getConfig0().getDamageFormat().formatted(packet.getFinalValue())).color(NamedTextColor.RED);
                } else {
                    value = Component.text(plugin.getConfig0().getDamageFormat().formatted(packet.getFinalValue()));
                }
                Component packetText = plugin.getLang()
                    .of("damage.packet_text")
                    .resolver(
                        Placeholder.component("value", value),
                        Placeholder.component("type", type)
                    ).locale(player).component();
                allPacketText.append(packetText);
                packetAppended = true;
            }
        }

        // Done, send the message to the player (attacker)!
        player.sendMessage(headText.append(allPacketText).append(tailText));
    }

    private boolean containsType(DamageType[] types, DamageType type) {
        for (DamageType dt : types) {
            if (dt == type) return true;
        }
        return false;
    }

}
