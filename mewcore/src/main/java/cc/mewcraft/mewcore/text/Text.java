package cc.mewcraft.mewcore.text;

import me.lucko.helper.utils.Players;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A wrapper class utilizing {@link Component} along with plain string language messages with convenience methods for
 * text replacements.
 */
@DefaultQualifier(Deprecated.class)
@DefaultQualifier(NonNull.class)
public class Text {

    private final TextRepository config;
    private Component internal;

    public Text(TextRepository settings, String content) {
        config = settings;
        internal = Component.text(content);
    }

    public static void sendTitle(Audience audience, Component title, Component subtitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        audience.showTitle(Title.title(title, subtitle, Title.Times.times(Ticks.duration(fadeInTicks), Ticks.duration(stayTicks), Ticks.duration(fadeOutTicks))));
    }

    public static void sendActionBar(Audience audience, Component text) {
        audience.sendActionBar(text);
    }

    public Text replace(String placeholder, Component replacement) {
        internal = internal.replaceText(builder -> builder.matchLiteral(concat(placeholder)).replacement(replacement));
        return this;
    }

    public Text replace(String placeholder, Component replacement, Consumer<Style.Builder> consumer) {
        internal = internal.replaceText(builder -> builder.matchLiteral(concat(placeholder)).replacement(replacement.style(consumer, Style.Merge.Strategy.IF_ABSENT_ON_TARGET)));
        return this;
    }

    public Text replace(String placeholder, Entity replacement) {
        internal = internal.replaceText(builder -> {
            final Component replacementNonnull = Optional.ofNullable(replacement.customName()).orElse(Component.text(replacement.getName()));
            builder.matchLiteral(concat(placeholder)).replacement(replacementNonnull);
        });
        return this;
    }

    public Text replace(String placeholder, Entity replacement, Consumer<Style.Builder> consumer) {
        internal = internal.replaceText(builder -> {
            final Component componentNonnull = Optional.ofNullable(replacement.customName()).orElse(Component.text(replacement.getName()));
            builder.matchLiteral(concat(placeholder)).replacement(componentNonnull.style(consumer, Style.Merge.Strategy.IF_ABSENT_ON_TARGET));
        });
        return this;
    }

    public Text replace(String placeholder, ItemStack replacement) {
        internal = internal.replaceText(builder -> builder.matchLiteral(concat(placeholder)).replacement(replacement.displayName()));
        return this;
    }

    public Text replace(String placeholder, ItemStack replacement, Consumer<Style.Builder> consumer) {
        internal = internal.replaceText(builder -> builder.matchLiteral(concat(placeholder)).replacement(replacement.displayName().style(consumer, Style.Merge.Strategy.IF_ABSENT_ON_TARGET)));
        return this;
    }

    public Text replace(String placeholder, Player replacement) {
        internal = internal.replaceText(builder -> builder.matchLiteral(concat(placeholder)).replacement(replacement.displayName()));
        return this;
    }

    public Text replace(String placeholder, Player replacement, Consumer<Style.Builder> consumer) {
        internal = internal.replaceText(builder -> builder.matchLiteral(concat(placeholder)).replacement(replacement.displayName().style(consumer, Style.Merge.Strategy.IF_ABSENT_ON_TARGET)));
        return this;
    }

    public Text replace(String placeholder, String replacement) {
        internal = internal.replaceText(builder -> builder.matchLiteral(concat(placeholder)).replacement(replacement));
        return this;
    }

    public Text replace(String placeholder, String replacement, Consumer<Style.Builder> consumer) {
        internal = internal.replaceText(builder -> builder.matchLiteral(concat(placeholder)).replacement(Component.text(replacement).style(consumer)));
        return this;
    }

    public Text replace(String placeholder, char replacement) {
        internal = internal.replaceText(builder -> builder.matchLiteral(concat(placeholder)).replacement(Component.text(replacement)));
        return this;
    }

    public Text replace(String placeholder, char replacement, Consumer<Style.Builder> consumer) {
        internal = internal.replaceText(builder -> builder.matchLiteral(concat(placeholder)).replacement(Component.text(replacement).style(consumer)));
        return this;
    }

    public Text replace(String placeholder, boolean replacement) {
        internal = internal.replaceText(builder -> builder.matchLiteral(concat(placeholder)).replacement(Component.text(replacement)));
        return this;
    }

    public Text replace(String placeholder, boolean replacement, Consumer<Style.Builder> consumer) {
        internal = internal.replaceText(builder -> builder.matchLiteral(concat(placeholder)).replacement(Component.text(replacement).style(consumer)));
        return this;
    }

    public Text replace(String placeholder, int replacement) {
        internal = internal.replaceText(builder -> builder.matchLiteral(concat(placeholder)).replacement(Component.text(replacement)));
        return this;
    }

    public Text replace(String placeholder, int replacement, Consumer<Style.Builder> consumer) {
        internal = internal.replaceText(builder -> builder.matchLiteral(concat(placeholder)).replacement(Component.text(replacement).style(consumer)));
        return this;
    }

    public Text replace(String placeholder, float replacement) {
        internal = internal.replaceText(builder -> builder.matchLiteral(concat(placeholder)).replacement(Component.text(("%." + config.decimalPrecision + "f").formatted(replacement))));
        return this;
    }

    public Text replace(String placeholder, float replacement, Consumer<Style.Builder> consumer) {
        internal = internal.replaceText(builder -> builder.matchLiteral(concat(placeholder)).replacement(Component.text(("%." + config.decimalPrecision + "f").formatted(replacement)).style(consumer)));
        return this;
    }

    public Text replace(String placeholder, double replacement) {
        internal = internal.replaceText(builder -> builder.matchLiteral(concat(placeholder)).replacement(Component.text(("%." + config.decimalPrecision + "f").formatted(replacement))));
        return this;
    }

    public Text replace(String placeholder, double replacement, Consumer<Style.Builder> consumer) {
        internal = internal.replaceText(builder -> builder.matchLiteral(concat(placeholder)).replacement(Component.text(("%." + config.decimalPrecision + "f").formatted(replacement)).style(consumer)));
        return this;
    }

    public Text append(Text text) {
        return append(text.asComponent());
    }

    public Text append(Component component) {
        internal = internal.append(component);
        return this;
    }

    public Component asComponent() {
        return internal;
    }

    public void to(Audience audience) {
        audience.sendMessage(internal);
    }

    public void to(Audience audience, MessageType messageType) {
        switch (messageType) {
            case CHAT -> to(audience);
            case TITLE -> sendTitle(audience, internal, Component.empty(), 10, 40, 10);
            case SUBTITLE -> sendTitle(audience, Component.empty(), internal, 10, 40, 10);
            case ACTION_BAR -> audience.sendActionBar(internal);
            case BOSS_BAR ->
                audience.showBossBar(BossBar.bossBar(internal, BossBar.MAX_PROGRESS, BossBar.Color.YELLOW, BossBar.Overlay.PROGRESS));
        }
    }

    public void broadcast() {
        Players.forEach(this::to);
    }

    public void broadcast(MessageType messageType) {
        Players.forEach(p -> to(p, messageType));
    }

    public void broadcast(Permission permission) {
        Players.forEach(p -> {
            if (p.hasPermission(permission)) to(p);
        });
    }

    public void broadcast(World world) {
        world.getPlayers().forEach(this::to);
    }

    public void broadcast(MessageType messageType, Predicate<Player> predicate) {
        Players.forEach(p -> {
            if (predicate.test(p)) {
                to(p, messageType);
            }
        });
    }

    private String concat(String placeholder) {
        return config.placeholderPrefix + placeholder + config.placeholderSuffix;
    }

    public enum MessageType {CHAT, ACTION_BAR, TITLE, SUBTITLE, BOSS_BAR}
}
