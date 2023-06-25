package cc.mewcraft.mewcore.message;

import de.themoep.utils.lang.bukkit.LanguageManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A sophisticated builder of all the translated messages,
 * additionally providing convenience methods to quickly
 * send message to an audience.
 */
@SuppressWarnings("unused")
public class Translations {

    private static final String placeholderPrefix = "<";
    private static final String placeholderSuffix = ">";
    private final LanguageManager lang;

    public Translations(final Plugin plugin) {
        this(plugin, "lang", "zh");
    }

    public Translations(final Plugin plugin, String folder) {
        this(plugin, folder, "zh");
    }

    public Translations(final Plugin plugin, String folder, String defaultLocale) {
        this.lang = new LanguageManager(plugin, folder, defaultLocale);
        this.lang.setProvider(sender -> {
            if (sender instanceof Player)
                return ((Player) sender).locale().getLanguage();
            else
                return null;
        });
    }

    public TranslationBuilder of(String key) {
        return new TranslationBuilder(lang, key);
    }

    public static class TranslationBuilder {
        private final LanguageManager lang;
        private final String key;
        private final Map<String, Object> substitutions;
        private final TagResolver.Builder tags;
        private Locale locale;

        public TranslationBuilder(final LanguageManager lang, final String key) {
            this.lang = lang;
            this.key = key;
            this.locale = Locale.CHINESE;
            this.substitutions = new HashMap<>();
            this.tags = TagResolver.builder();
        }

        public TranslationBuilder replace(String key, Object value) {
            this.substitutions.put(
                Translations.placeholderPrefix + key + Translations.placeholderSuffix,
                value
            );
            return this;
        }

        public TranslationBuilder replace(String... replacements) {
            for (int i = 0; i + 1 < replacements.length; i += 2) {
                if (replacements[i] == null)
                    continue;
                this.substitutions.put(
                    Translations.placeholderPrefix + replacements[i] + Translations.placeholderSuffix,
                    replacements[i + 1]
                );
            }
            return this;
        }

        public TranslationBuilder resolver(TagResolver resolver) {
            this.tags.resolver(resolver);
            return this;
        }

        public TranslationBuilder resolver(TagResolver... resolvers) {
            this.tags.resolvers(resolvers);
            return this;
        }

        public TranslationBuilder locale(Audience audience) {
            if (audience instanceof Player player)
                this.locale = player.locale();
            return this;
        }

        public TranslationBuilder locale(Locale locale) {
            this.locale = locale;
            return this;
        }

        /**
         * Gets the plain string of the translated message.
         * <p>
         * This is a terminal operation.
         *
         * @return the translated message with string replacements applied
         */
        public String plain() {
            String string = this.lang.getConfig(this.locale.getLanguage()).get(this.key);
            for (Map.Entry<String, Object> entry : this.substitutions.entrySet()) {
                string = string.replace(entry.getKey(), String.valueOf(entry.getValue()));
            }
            return string;
        }

        /**
         * Gets the component of the translated message, using MiniMessage deserializer, with all string replacements
         * and tags applied.
         * <p>
         * This is a terminal operation.
         *
         * @return the translated message with string replacements and tags applied
         */
        public Component component() {
            return MiniMessage.miniMessage().deserialize(plain(), this.tags.build());
        }

        /**
         * Sends this translated message to the viewer through chat.
         * <p>
         * This is a terminal operation.
         */
        public void send(Audience audience) {
            locale(audience);
            audience.sendMessage(component());
        }

        /**
         * Sends this translated message to the viewer through title.
         * <p>
         * This is a terminal operation.
         */
        public void title(Audience audience) {
            locale(audience);
            audience.showTitle(Title.title(Component.empty(), component()));
        }

        /**
         * Sends this translated message to the viewer through action bar.
         * <p>
         * This is a terminal operation.
         */
        public void actionBar(Audience audience) {
            locale(audience);
            audience.sendActionBar(component());
        }

    }

}