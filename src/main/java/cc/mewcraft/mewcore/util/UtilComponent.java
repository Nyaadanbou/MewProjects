package cc.mewcraft.mewcore.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class UtilComponent {

    public static final Pattern PERCENT_PATTERN   = Pattern.compile("%([^%]+)%");
    public static final Pattern BRACKET_PATTERN   = Pattern.compile("[{]([^{}]+)[}]");

    /**
     * Converts the MiniMessage string into a component.
     * <p>
     * It will return a {@link Component#empty()} if the given string is null.
     *
     * @param miniMessage a MiniMessage string
     *
     * @return a component
     */
    @Contract(pure = true)
    public static @NotNull Component asComponent(@Nullable String miniMessage) {
        if (miniMessage == null) return Component.empty();
        return MiniMessage.miniMessage().deserialize(miniMessage);
    }

    @Contract(pure = true)
    public static @NotNull Component asComponent(@Nullable String miniMessage, TagResolver resolver) {
        if (miniMessage == null) return Component.empty();
        return MiniMessage.miniMessage().deserialize(miniMessage, resolver);
    }

    /**
     * Converts the list of MiniMessage strings into a list of components.
     * <p>
     * It will return a {@link List#of()} if the given string is null.
     *
     * @param miniMessage a list of a MiniMessage strings
     *
     * @return an unmodifiable list of components
     */
    @Contract(pure = true)
    public static @NotNull List<Component> asComponent(@Nullable List<String> miniMessage) {
        if (miniMessage == null) return List.of();
        return miniMessage.stream().map(UtilComponent::asComponent).toList();
    }

    @Contract(pure = true)
    public static @NotNull List<Component> asComponent(@Nullable List<String> miniMessage, TagResolver resolver) {
        if (miniMessage == null) return List.of();
        return miniMessage.stream().map(string -> asComponent(string, resolver)).toList();
    }

    /**
     * Converts the component into a MiniMessage string.
     * <p>
     * It will return an empty string if the given component is null.
     *
     * @param component a component
     *
     * @return a string in MiniMessage representation
     */
    @Contract(pure = true)
    public static @NotNull String asMiniMessage(@Nullable Component component) {
        if (component == null) return "";
        return MiniMessage.miniMessage().serialize(component.compact());
    }

    /**
     * Converts the list of components into a list of MiniMessage strings.
     * <p>
     * It will return a {@link List#of()} if the given list is null.
     *
     * @param component a list of components
     *
     * @return an unmodifiable list of strings in MiniMessage representation
     */
    @Contract(pure = true)
    public static @NotNull List<String> asMiniMessage(@Nullable List<Component> component) {
        if (component == null) return List.of();
        return component.stream().map(UtilComponent::asMiniMessage).toList();
    }

    /**
     * Converts the component into a plain text, without any text decorations.
     *
     * @param component a component
     *
     * @return a plain text
     */
    @Contract(pure = true)
    public static @NotNull String asPlainText(@NotNull Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    /**
     * Removes all MiniMessage tags from given text.
     *
     * @param miniMessage a text in MiniMessage format
     *
     * @return a plain text
     */
    @Contract(pure = true)
    public static @NotNull String stripTags(@NotNull String miniMessage) {
        return MiniMessage.miniMessage().stripTags(miniMessage);
    }

    /**
     * Transforms any group of empty components found in a row into just one empty component.
     * <p>
     * Empty components are those where the plain content string is "".
     *
     * @param componentList a list of components which may contain empty lines
     *
     * @return a mutable modified copy of the list
     */
    @Contract(pure = true)
    public static @NotNull List<Component> compressEmptyLines(@NotNull List<Component> componentList) {
        List<Component> stripped = new ArrayList<>();
        boolean prevEmpty = false; // Mark whether the previous line is empty
        for (Component line : componentList) {
            String plainLine = asPlainText(line);
            if (plainLine.isEmpty()) {
                if (!prevEmpty) {
                    prevEmpty = true;
                    stripped.add(line);
                }
            } else {
                prevEmpty = false;
                stripped.add(line);
            }
        }
        return stripped;
    }

    @Contract(pure = true)
    public static @NotNull Component replace(@NotNull Component component, String literal, String replacement) {
        return replace(component, literal, asComponent(replacement));
    }

    @Contract(pure = true)
    public static @NotNull Component replace(@NotNull Component component, String literal, Component replacement) {
        return component.replaceText(config -> config
            .matchLiteral(literal)
            .replacement(replacement)
        );
    }

    /**
     * Applies the string replacer to given component.
     *
     * @param replacer  a string replacer
     * @param component a component which the string replacer applies to
     *
     * @return a modified copy of the component
     */
    @SafeVarargs
    @Contract(pure = true)
    public static @NotNull Component replace(@NotNull Component component, @NotNull UnaryOperator<String>... replacer) {
        return component.replaceText(config -> config
            .match(PERCENT_PATTERN)
            .replacement((matchResult, builder) -> {
                String replaced = matchResult.group();
                for (final UnaryOperator<String> re : replacer) {
                    replaced = re.apply(replaced);
                }
                return asComponent(replaced);
            })
        );
    }

    /**
     * Applies the string replacer to each component of the list.
     *
     * @param replacer      a string replacer
     * @param componentList a list of components which the string replacer applies to
     *
     * @return a mutable modified copy of the list
     */
    @SafeVarargs
    @Contract(pure = true)
    public static @NotNull List<Component> replace(@NotNull List<Component> componentList, @NotNull UnaryOperator<String>... replacer) {
        List<Component> replaced = new ArrayList<>();
        for (Component line : componentList) {
            replaced.add(replace(line, replacer));
        }
        return replaced;
    }

    /**
     * @deprecated in favor of {@link #replacePlaceholderList(String, List, List)}
     */
    @Deprecated
    @Contract(pure = true)
    public static @NotNull List<Component> replace(@NotNull List<Component> original, @NotNull String placeholder, boolean keep, Component... replacer) {
        return replacePlaceholderList(placeholder, original, Arrays.asList(replacer));
    }

    /**
     * Modifies the list of components such that the given placeholder is replaced by the given replacer.
     *
     * @param oldList     the list of components to which the replacement is applied
     * @param placeholder the placeholder contained in the list of components
     * @param keep        true to keep other contents around the placeholder
     * @param replacer    the new list of components replacing the placeholder
     *
     * @return a modified copy of the list
     *
     * @deprecated in favor of {@link #replacePlaceholderList(String, List, List)}
     */
    @Deprecated
    @Contract(pure = true)
    public static @NotNull List<Component> replace(@NotNull List<Component> oldList, @NotNull String placeholder, boolean keep, List<Component> replacer) {
        return replacePlaceholderList(placeholder, oldList, replacer);
    }

    /**
     * Inserts the src list into the dst list, at the position where the given placeholder presents.
     * <p>
     * Note that only the first placeholder encountered will be replaced.
     *
     * @param placeholder the placeholder in the lore
     * @param dst         the lore which contains the placeholder to be modified
     * @param src         the lore to be copied and inserted into the dst lore
     *
     * @return a mutable modified copy of the dst list
     */
    @Contract(pure = true, value = "_, null, _ -> null; _, !null, _ -> !null ")
    public static List<Component> replacePlaceholderList(@NotNull String placeholder, @Nullable List<Component> dst, @NotNull List<Component> src) {
        return replacePlaceholderList(placeholder, dst, src, false);
    }

    /**
     * Inserts the src list into the dst list, at the position where the given placeholder presents.
     * <p>
     * Note that only the first placeholder encountered will be replaced.
     *
     * @param placeholder the placeholder in the lore
     * @param dst         the lore which contains the placeholder to be modified
     * @param src         the lore to be copied and inserted into the dst lore
     * @param keep        true to keep the other content around the placeholder
     *
     * @return a mutable modified copy of the dst list
     */
    @Contract(pure = true, value = "_, null, _, _ -> null; _, !null, _, _ -> !null ")
    public static List<Component> replacePlaceholderList(@NotNull String placeholder, @Nullable List<Component> dst, @NotNull List<Component> src, boolean keep) {
        if (dst == null) return null;

        // Let's find which line (in the dst) has the placeholder
        int placeholderIdx = -1;
        Component placeholderLine = null;
        for (int i = 0; i < dst.size(); i++) {
            placeholderLine = dst.get(i);
            // Component is complex. We use plain text to find the pos of the placeholder
            if (asPlainText(placeholderLine).contains(placeholder)) {
                placeholderIdx = i;
                break;
            }
        }
        if (placeholderIdx == -1) return dst;

        // Let's make the list to be inserted into the dst
        if (keep) {
            src = new ArrayList<>(src);
            ListIterator<Component> it = src.listIterator();
            while (it.hasNext()) {
                Component line = it.next();
                Component replaced = replace(placeholderLine, placeholder, line);
                it.set(replaced);
            }
        }

        // Insert the src into the dst
        List<Component> result = new ArrayList<>(dst);
        result.remove(placeholderIdx); // Need to remove the raw placeholder from dst
        result.addAll(placeholderIdx, src);

        return result;
    }

    @Contract(pure = true)
    public static @NotNull Component removeItalic(@NotNull Component component) {
        return component.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    @Contract(pure = true)
    public static @NotNull List<Component> removeItalic(@NotNull List<Component> component) {
        return component.stream().map(line -> {
            if (Component.empty().equals(line))
                return line;
            else
                return UtilComponent.removeItalic(line);
        }).toList();
    }

}
