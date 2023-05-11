package co.mcsky.mmoext.damage.indicator;

import co.mcsky.mmoext.Main;
import co.mcsky.mmoext.damage.PlayerAttackHandler;
import co.mcsky.mmoext.damage.crit.CriticalHitManager;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.damage.DamagePacket;
import io.lumine.mythic.lib.damage.DamageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * This handler shows damage information to the player who attacks certain MM mobs.
 */
public class DamageIndicatorHandler implements PlayerAttackHandler {

    @Override
    public void handle(@NotNull PlayerAttackEvent event, @NotNull ActiveMob activeMob) {
        DamageIndicatorParams params = DamageIndicatorManager.getInstance().getParams(activeMob.getType());
        if (params == null) return; // If the MM mob isn't being tracked

        DamageMetadata damageMeta = event.getDamage();
        Player player = event.getAttacker().getPlayer();

        Component headText = Main.lang().getMiniMessage(player, "damage.headText").replaceText(config -> config.matchLiteral("{damagee}").replacement(activeMob.getDisplayName()));
        Component tailText = Main.lang().getMiniMessage(player, "damage.tailText");
        Component packetSeparator = Main.lang().getMiniMessage(player, "damage.packetSeparator");
        Component typeSeparator = Main.lang().getMiniMessage(player, "damage.typeSeparator");

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
                    type.append(Main.lang().getMiniMessage(player, "damage.type." + requiredType.name().toLowerCase()));
                    typeAppended = true;
                }
            }
            if (typeAppended) { // If this DamagePacket contains the DamageType we want
                Component value;
                if (CriticalHitManager.isVanillaCriticalHit(event) || damageMeta.isWeaponCriticalStrike() || damageMeta.isSkillCriticalStrike()) {
                    value = Component.text(Main.config().getDamageFormat().formatted(packet.getFinalValue())).color(NamedTextColor.RED);
                } else {
                    value = Component.text(Main.config().getDamageFormat().formatted(packet.getFinalValue()));
                }
                Component packetText = Main.lang().getMiniMessage(player, "damage.packetText")
                        .replaceText(config -> config.matchLiteral("{value}").replacement(value))
                        .replaceText(config -> config.matchLiteral("{type}").replacement(type));
                allPacketText.append(packetText);
                packetAppended = true;
            }
        }

        // Done, send the message to the player (attacker)!
        player.sendMessage(headText.append(allPacketText).append(tailText));
    }

    private boolean containsType(DamageType[] types, DamageType type) {
        for (DamageType d1 : types) {
            if (d1 == type) return true;
        }
        return false;
    }

}
