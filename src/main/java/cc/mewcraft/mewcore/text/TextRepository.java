package cc.mewcraft.mewcore.text;

import org.bukkit.ChatColor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.function.Function;

@DefaultQualifier(Deprecated.class)
@DefaultQualifier(NonNull.class)
public class TextRepository {

    protected String placeholderPrefix = "{";
    protected String placeholderSuffix = "}";
    protected int decimalPrecision = 3;
    protected Function<String, String> provider;

    /**
     * @param provider the provider of message dictionary
     */
    public TextRepository(Function<String, String> provider) {
        this.provider = provider;
    }

    public void placeholderPrefix(String prefix) {
        placeholderPrefix = prefix;
    }

    public void placeholderSuffix(String suffix) {
        placeholderSuffix = suffix;
    }

    public void decimalPrecision(int precision) {
        decimalPrecision = precision;
    }

    public Text get(String key) {
        return new Text(this, ChatColor.translateAlternateColorCodes('&', provider.apply(key)));
    }

}

