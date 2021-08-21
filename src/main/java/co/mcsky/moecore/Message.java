package co.mcsky.moecore;

import me.lucko.helper.utils.Players;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.title.Title;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.function.Predicate;

public class Message {

    private static String placeholderPrefix;
    private static String placeholderSuffix;
    private static int decimalPrecision;
    private Component inner;

    private Message(String content) {
        placeholderPrefix = "{";
        placeholderSuffix = "}";
        decimalPrecision = 3;
        inner = Component.text(content);
    }

    public static void setPlaceholderPrefix(String prefix) {
        placeholderPrefix = prefix;
    }

    public static void setPlaceholderSuffix(String suffix) {
        placeholderSuffix = suffix;
    }

    public static void setDecimalPrecision(int precision) {
        decimalPrecision = precision;
    }

    public static Message of(String content) {
        return new Message(content);
    }

    static String concat(String placeholder) {
        return placeholderPrefix + placeholder + placeholderSuffix;
    }

    public static void sendTitle(Audience audience, Component title, Component subtitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        audience.showTitle(Title.title(title, subtitle, Title.Times.of(Duration.ofMillis(500), Duration.ofMillis(2000), Duration.ofMillis(500))));
    }

    public Message replace(@NotNull String placeholder, @NotNull ItemStack replacement) {
        return replace(placeholder, replacement, Style.empty());
    }

    public Message replace(@NotNull String placeholder, @NotNull ItemStack replacement, Style style) {
        inner = inner.replaceText(builder -> builder.matchLiteral(concat(placeholder)).replacement(replacement.displayName().style(style)));
        return this;
    }

    public Message replace(@NotNull String placeholder, @NotNull Player replacement) {
        return replace(placeholder, replacement, Style.empty());
    }

    public Message replace(@NotNull String placeholder, @NotNull Player replacement, Style style) {
        inner = inner.replaceText(builder -> builder.matchLiteral(concat(placeholder)).replacement(replacement.displayName().style(style)));
        return this;
    }

    public Message replace(@NotNull String placeholder, @NotNull String replacement) {
        return replace(placeholder, replacement, Style.empty());
    }

    public Message replace(@NotNull String placeholder, @NotNull String replacement, Style style) {
        inner = inner.replaceText(builder -> builder.matchLiteral(concat(placeholder)).replacement(Component.text(replacement).style(style)));
        return this;
    }

    public Message replace(@NotNull String placeholder, char replacement) {
        return replace(placeholder, replacement, Style.empty());
    }

    public Message replace(@NotNull String placeholder, char replacement, Style style) {
        inner = inner.replaceText(builder -> builder.matchLiteral(concat(placeholder)).replacement(Component.text(replacement).style(style)));
        return this;
    }

    public Message replace(@NotNull String placeholder, boolean replacement) {
        return replace(placeholder, replacement, Style.empty());
    }


    public Message replace(@NotNull String placeholder, boolean replacement, Style style) {
        inner = inner.replaceText(builder -> builder.matchLiteral(concat(placeholder)).replacement(Component.text(replacement).style(style)));
        return this;
    }

    public Message replace(@NotNull String placeholder, int replacement) {
        return replace(placeholder, replacement, Style.empty());
    }


    public Message replace(@NotNull String placeholder, int replacement, Style style) {
        inner = inner.replaceText(builder -> builder.matchLiteral(concat(placeholder)).replacement(Component.text(replacement).style(style)));
        return this;
    }

    public Message replace(@NotNull String placeholder, float replacement) {
        return replace(placeholder, replacement, Style.empty());
    }


    public Message replace(@NotNull String placeholder, float replacement, Style style) {
        inner = inner.replaceText(builder -> builder.matchLiteral(concat(placeholder)).replacement(Component.text(("%." + decimalPrecision + "f").formatted(replacement)).style(style)));
        return this;
    }

    public Message replace(@NotNull String placeholder, double replacement) {
        return replace(placeholder, replacement, Style.empty());
    }


    public Message replace(@NotNull String placeholder, double replacement, Style style) {
        inner = inner.replaceText(builder -> builder.matchLiteral(concat(placeholder)).replacement(Component.text(("%." + decimalPrecision + "f").formatted(replacement)).style(style)));
        return this;
    }

    public Component asComponent() {
        return inner;
    }

    public void to(Audience audience) {
        audience.sendMessage(inner);
    }

    public void to(Audience audience, MessageType messageType) {
        switch (messageType) {
            case CHAT -> to(audience);
            case TITLE -> sendTitle(audience, inner, Component.empty(), 10, 40, 10);
            case SUBTITLE -> sendTitle(audience, Component.empty(), inner, 10, 40, 10);
            case ACTION_BAR -> audience.sendActionBar(inner);
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

    public enum MessageType {CHAT, ACTION_BAR, TITLE, SUBTITLE}

}
